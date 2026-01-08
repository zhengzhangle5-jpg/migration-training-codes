package com.migration.day13;

import java.sql.*;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

public class BatchInsertOptimizedExample {

    public static void main(String[] args) throws Exception {

        HikariConfig config = new HikariConfig();
        config.setJdbcUrl("jdbc:snowflake://PCNZPCZ-QB93968.snowflakecomputing.com");
        config.setUsername("zzl");
        config.setPassword("20030828zzlZzl");
        config.addDataSourceProperty("warehouse", "COMPUTE_WH");
        config.addDataSourceProperty("db", "MIGRATION_TRAINING");
        config.addDataSourceProperty("schema", "PRACTICE");

        // ✅ 修改点 1：
        // 启用 Snowflake JDBC 的批量写入能力（默认就是 true，这里显式说明）
        // 原 case2 中通过 useBulkCopy=false 强制退化为逐行 INSERT
        config.addDataSourceProperty("useBulkCopy", "true");

        HikariDataSource ds = new HikariDataSource(config);

        try (Connection conn = ds.getConnection()) {

            conn.setAutoCommit(false);

            Statement stmt = conn.createStatement();
            stmt.execute(
                    "CREATE OR REPLACE TABLE users_perf (" +
                            "user_id NUMBER, user_name STRING)"
            );

            PreparedStatement ps =
                    conn.prepareStatement(
                            "INSERT INTO users_perf (user_id, user_name) VALUES (?, ?)"
                    );

            long start = System.currentTimeMillis();

            // 批量大小（教学用，方便观察）
            final int BATCH_SIZE = 100;

            for (int i = 1; i <= 300; i++) {

                ps.setInt(1, i);
                ps.setString(2, "USER_" + i);

                // ❌ 原 case2：逐行执行（性能极差）
                // ps.executeUpdate();

                // ✅ 修改点 2：
                // 改为 addBatch，将多行 INSERT 合并发送
                ps.addBatch();

                // ✅ 修改点 3：
                // 每到一个 batch size 执行一次批量提交
                if (i % BATCH_SIZE == 0) {
                    ps.executeBatch();
                    ps.clearBatch();
                }
            }

            // ✅ 修改点 4：
            // 处理最后不足一个 batch 的数据
            ps.executeBatch();

            conn.commit();

            long end = System.currentTimeMillis();
            System.out.println("Total Time(ms): " + (end - start));
        }

        ds.close();
    }
}
