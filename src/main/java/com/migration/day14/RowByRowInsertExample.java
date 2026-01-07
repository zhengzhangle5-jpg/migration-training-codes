package com.migration.day14;

import java.sql.*;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

public class RowByRowInsertExample {

    public static void main(String[] args) throws Exception {

        HikariConfig config = new HikariConfig();
        config.setJdbcUrl("jdbc:snowflake://PCNZPCZ-QB93968.snowflakecomputing.com");
        config.setUsername("zzl");
        config.setPassword("20030828zzlZzl");
        config.addDataSourceProperty("warehouse", "COMPUTE_WH");
        config.addDataSourceProperty("db", "MIGRATION_TRAINING");
        config.addDataSourceProperty("schema", "PRACTICE");
        //修改1：明确禁用批处理
        config.addDataSourceProperty("useBulkCopy", "false");


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

            for (int i = 1; i <= 300; i++) {

                ps.setInt(1, i);
                ps.setString(2, "USER_" + i);

                // ❌ 逐行执行
                ps.executeUpdate();
            }

            conn.commit();

            long end = System.currentTimeMillis();
            System.out.println("Total Time(ms): " + (end - start));
        }

        ds.close();
    }
}
