package com.migration.day13;

import java.sql.*;
import java.util.concurrent.*;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

public class ConnectionPoolOptimizedExample {

    public static void main(String[] args) throws Exception {

        HikariConfig config = new HikariConfig();
        config.setJdbcUrl("jdbc:snowflake://PCNZPCZ-QB93968.snowflakecomputing.com");
        config.setUsername("zzl");
        config.setPassword("20030828zzlZzl");
        config.addDataSourceProperty("warehouse", "COMPUTE_WH");
        config.addDataSourceProperty("db", "MIGRATION_TRAINING");
        config.addDataSourceProperty("schema", "PRACTICE");

        // ❌ 原 case3：
        // config.setMaximumPoolSize(5);

        // ✅ 修改点 1：连接池大小与并发度匹配
        // 原来 20 个并发线程抢 5 个连接，必然 timeout
        config.setMaximumPoolSize(20);

        // ✅ 修改点 2：合理的连接等待时间
        // 避免短时间抖动就抛 timeout
        config.setConnectionTimeout(30000); // 30 秒

        // ✅ 修改点 3：Snowflake 场景推荐关闭 minimumIdle 预热
        config.setMinimumIdle(0);

        HikariDataSource ds = new HikariDataSource(config);

        // ❌ 原 case3：线程池远大于连接池
        // ExecutorService executor = Executors.newFixedThreadPool(20);

        // ✅ 修改点 4：线程池大小与连接池一致
        ExecutorService executor = Executors.newFixedThreadPool(20);

        long start = System.currentTimeMillis();

        for (int i = 0; i < 20; i++) {
            executor.submit(() -> {

                try (Connection conn = ds.getConnection()) {

                    // （可选）关闭 Result Cache，确保每次都是实际查询
                    try (Statement stmt = conn.createStatement()) {
                        stmt.execute(
                                "ALTER SESSION SET USE_CACHED_RESULT = FALSE"
                        );
                    }

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
                    // ✅ 优化后：理论上不应再出现 timeout
                    System.err.println(
                            Thread.currentThread().getName() +
                                    " -> ERROR: " + e.getMessage()
                    );
                }
            });
        }

        executor.shutdown();
        executor.awaitTermination(5, TimeUnit.MINUTES);

        long end = System.currentTimeMillis();
        System.out.println("Total Execution Time(ms): " + (end - start));

        ds.close();
    }
}
