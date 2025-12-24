package com.migration.day08.bug5;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

public class Bug5Main {

    public static void main(String[] args) throws Exception {
        System.out.println("=== Bug 5: Mixed Oracle & Snowflake Pool ===");

        // ❌ 错误：混用连接池
        try {
            DataSource mixed = Bug5MixedPoolFactory.createSnowflakeDataSource();
            test(mixed);
        } catch (Exception e) {
            System.out.println("[EXPECTED FAILURE - MIXED POOL]");
            e.printStackTrace(System.out);
        }

        System.out.println("---- Correct Snowflake Pool ----");

        // ✅ 正确：Snowflake 专用连接池
        DataSource snowflake =
                Bug5SnowflakeDataSourceFactory.createDataSource();
        test(snowflake);

        System.out.println("=== Bug 5 Finished ===");
    }

    private static void test(DataSource ds) throws Exception {
        try (Connection c = ds.getConnection();
             Statement st = c.createStatement();
             ResultSet rs = st.executeQuery(
                     "select current_user(), current_role(), current_timestamp()")) {

            while (rs.next()) {
                System.out.println("[RESULT] user=" + rs.getString(1)
                        + ", role=" + rs.getString(2)
                        + ", ts=" + rs.getString(3));
            }
        }
    }
}

