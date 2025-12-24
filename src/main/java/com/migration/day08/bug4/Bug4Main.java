package com.migration.day08.bug4;

import javax.sql.DataSource;
import java.sql.Connection;

public class Bug4Main {

    public static void main(String[] args) throws Exception {
        System.out.println("=== Bug 4: Transaction Isolation Level ===");

        // Oracle 风格（错误示范）
        DataSource oracleStyleDs =
                Bug4OracleStyleDataSourceFactory.createDataSource();

        try (Connection conn = oracleStyleDs.getConnection()) {
            Bug4TransactionDemo.run(conn, "Oracle-Style (Wrong)");
        }
//        try {
//            DataSource oracleStyleDs =
//                    Bug4OracleStyleDataSourceFactory.createDataSource();
//            oracleStyleDs.getConnection();
//        } catch (Exception e) {
//            System.out.println("[EXPECTED FAILURE]");
//            e.printStackTrace(System.out);
//        }


        System.out.println("---- Switch to Snowflake Safe Config ----");

        // Snowflake 正确方式
        DataSource snowflakeSafeDs =
                Bug4SnowflakeSafeDataSourceFactory.createDataSource();

        try (Connection conn = snowflakeSafeDs.getConnection()) {
            Bug4TransactionDemo.run(conn, "Snowflake-Safe (Correct)");
        }

        System.out.println("=== Bug 4 Finished ===");
    }
}
