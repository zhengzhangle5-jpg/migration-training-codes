package com.migration.day01;

import java.sql.*;

public class DatabaseComparisonTest {
    // Oracle 连接信息
    private static final String ORACLE_URL = "jdbc:oracle:thin:@//192.168.0.120:1521/XEPDB1";
    private static final String ORACLE_USER = "migration_user";
    private static final String ORACLE_PASSWORD = "MigrationPass123";

    // Snowflake 连接信息
    private static final String SNOWFLAKE_URL = "jdbc:snowflake://PCNZPCZ-QB93968.snowflakecomputing.com/?db=MIGRATION_TRAINING&schema=PRACTICE&warehouse=COMPUTE_WH";
    private static final String SNOWFLAKE_USER = "zzl";
    private static final String SNOWFLAKE_PASSWORD = "20030828zzlZzl";

    public static void main(String[] args) {
        System.out.println("=== Oracle vs Snowflake Comparison Test ===\n");

        // 测试 1: 查询 EMPLOYEES 表的行数
        System.out.println("Test 1: Row Count Comparison");
        compareRowCount();

        // 测试 2: 查询 EMPLOYEES 表的数据
        System.out.println("\nTest 2: Data Comparison");
        compareData();

        // 测试 3: 对比当前时间函数
        System.out.println("\nTest 3: Current Timestamp Function");
        compareTimestamp();
    }

    private static void compareRowCount() {
        try (Connection oracleConn = DriverManager.getConnection(ORACLE_URL, ORACLE_USER, ORACLE_PASSWORD);
             Connection snowflakeConn = DriverManager.getConnection(SNOWFLAKE_URL, SNOWFLAKE_USER, SNOWFLAKE_PASSWORD)) {

            // Oracle 查询
            Statement oracleStmt = oracleConn.createStatement();
            ResultSet oracleRs = oracleStmt.executeQuery("SELECT COUNT(*) FROM EMPLOYEES");
            oracleRs.next();
            int oracleCount = oracleRs.getInt(1);

            // Snowflake 查询
            Statement snowflakeStmt = snowflakeConn.createStatement();
            ResultSet snowflakeRs = snowflakeStmt.executeQuery("SELECT COUNT(*) FROM EMPLOYEES");
            snowflakeRs.next();
            int snowflakeCount = snowflakeRs.getInt(1);

            System.out.println("  Oracle Row Count: " + oracleCount);
            System.out.println("  Snowflake Row Count: " + snowflakeCount);
            System.out.println("  Match: " + (oracleCount == snowflakeCount ? "✅ YES" : "❌ NO"));

            oracleRs.close();
            snowflakeRs.close();
            oracleStmt.close();
            snowflakeStmt.close();
        } catch (SQLException e) {
            System.out.println("  ❌ Error: " + e.getMessage());
        }
    }

    private static void compareData() {
        try (Connection oracleConn = DriverManager.getConnection(ORACLE_URL, ORACLE_USER, ORACLE_PASSWORD);
             Connection snowflakeConn = DriverManager.getConnection(SNOWFLAKE_URL, SNOWFLAKE_USER, SNOWFLAKE_PASSWORD)) {

            // Oracle 查询
            Statement oracleStmt = oracleConn.createStatement();
            ResultSet oracleRs = oracleStmt.executeQuery("SELECT EMPLOYEE_ID, FIRST_NAME, LAST_NAME FROM EMPLOYEES ORDER BY EMPLOYEE_ID");

            System.out.println("  Oracle Data:");
            while (oracleRs.next()) {
                System.out.println("    " + oracleRs.getInt("EMPLOYEE_ID") + " | " +
                        oracleRs.getString("FIRST_NAME") + " " +
                        oracleRs.getString("LAST_NAME"));
            }

            // Snowflake 查询
            Statement snowflakeStmt = snowflakeConn.createStatement();
            ResultSet snowflakeRs = snowflakeStmt.executeQuery("SELECT EMPLOYEE_ID, FIRST_NAME, LAST_NAME FROM EMPLOYEES ORDER BY EMPLOYEE_ID");

            System.out.println("\n  Snowflake Data:");
            while (snowflakeRs.next()) {
                System.out.println("    " + snowflakeRs.getInt("EMPLOYEE_ID") + " | " +
                        snowflakeRs.getString("FIRST_NAME") + " " +
                        snowflakeRs.getString("LAST_NAME"));
            }

            oracleRs.close();
            snowflakeRs.close();
            oracleStmt.close();
            snowflakeStmt.close();
        } catch (SQLException e) {
            System.out.println("  ❌ Error: " + e.getMessage());
        }
    }

    private static void compareTimestamp() {
        try (Connection oracleConn = DriverManager.getConnection(ORACLE_URL, ORACLE_USER, ORACLE_PASSWORD);
             Connection snowflakeConn = DriverManager.getConnection(SNOWFLAKE_URL, SNOWFLAKE_USER, SNOWFLAKE_PASSWORD)) {

            // Oracle: SYSDATE
            Statement oracleStmt = oracleConn.createStatement();
            ResultSet oracleRs = oracleStmt.executeQuery("SELECT SYSDATE FROM DUAL");
            oracleRs.next();
            System.out.println("  Oracle SYSDATE: " + oracleRs.getTimestamp(1));

            // Snowflake: CURRENT_TIMESTAMP()
            Statement snowflakeStmt = snowflakeConn.createStatement();
            ResultSet snowflakeRs = snowflakeStmt.executeQuery("SELECT CURRENT_TIMESTAMP()");
            snowflakeRs.next();
            System.out.println("  Snowflake CURRENT_TIMESTAMP(): " + snowflakeRs.getTimestamp(1));

            oracleRs.close();
            snowflakeRs.close();
            oracleStmt.close();
            snowflakeStmt.close();
        } catch (SQLException e) {
            System.out.println("  ❌ Error: " + e.getMessage());
        }
    }
}
