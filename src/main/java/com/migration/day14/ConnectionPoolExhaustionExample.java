package com.migration.day14;

import java.sql.*;
import java.util.concurrent.*;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

public class ConnectionPoolExhaustionExample {

    public static void main(String[] args) throws Exception {

        HikariConfig config = new HikariConfig();
        config.setJdbcUrl("jdbc:snowflake://PCNZPCZ-QB93968.snowflakecomputing.com");
        config.setUsername("zzl");
        config.setPassword("20030828zzlZzl");
        config.addDataSourceProperty("warehouse", "COMPUTE_WH");
        config.addDataSourceProperty("db", "MIGRATION_TRAINING");
        config.addDataSourceProperty("schema", "PRACTICE");

        // ❌ 故意设置很小的连接池
        config.setMaximumPoolSize(5);
        config.setConnectionTimeout(1000); // 3 秒超时

        HikariDataSource ds = new HikariDataSource(config);

        // ❌ 并发线程数远大于连接池
        ExecutorService executor = Executors.newFixedThreadPool(20);

        for (int i = 0; i < 20; i++) {
            executor.submit(() -> {

                try (Connection conn = ds.getConnection()) {

                    // ✅ 关键修改 1：禁用 Result Cache（每个连接都要）
                    try (Statement stmt = conn.createStatement()) {
                        stmt.execute(
                                "ALTER SESSION SET USE_CACHED_RESULT = FALSE"
                        );
                    }

                    // ✅ 关键修改 2：执行真实、耗时的查询
                    try (PreparedStatement ps =
                                 conn.prepareStatement(
                                         "SELECT COUNT(*) " +
                                                 "FROM orders_perf " +
                                                 "WHERE order_date >= '2025-01-01'"
                                 );
                         ResultSet rs = ps.executeQuery()) {

                        rs.next();
                        System.out.println(
                                Thread.currentThread().getName() +
                                        " -> result = " + rs.getLong(1)
                        );
                    }

                } catch (Exception e) {
                    // ❌ 预期会出现大量 timeout / pool exhausted
                    System.err.println(
                            Thread.currentThread().getName() +
                                    " -> ERROR: " + e.getMessage()
                    );
                }
            });
        }

        executor.shutdown();
        executor.awaitTermination(5, TimeUnit.MINUTES);

        ds.close();
    }
}
