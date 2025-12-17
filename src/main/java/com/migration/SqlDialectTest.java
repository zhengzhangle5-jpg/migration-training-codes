package com.migration;

import java.sql.*;

public class SqlDialectTest {

    // Oracle 连接信息
    private static final String ORACLE_URL = "jdbc:oracle:thin:@//192.168.0.120:1521/XEPDB1";
    private static final String ORACLE_USER = "migration_user";
    private static final String ORACLE_PASSWORD = "MigrationPass123";

    // Snowflake 连接信息
    private static final String SNOWFLAKE_URL =
            "jdbc:snowflake://PCNZPCZ-QB93968.snowflakecomputing.com/?db=MIGRATION_TRAINING&schema=PRACTICE&warehouse=COMPUTE_WH";
    private static final String SNOWFLAKE_USER = "zzl";
    private static final String SNOWFLAKE_PASSWORD = "20030828zzlZzl";

    public static void main(String[] args) {
        System.out.println("=== Oracle vs Snowflake SQL Behavior Comparison ===\n");

        testPseudoTable();
        testDateFunction();
        testEmptyStringBehavior();
        testCaseSensitivity();
    }

    /**
     * 测试 1：伪表 DUAL
     */
    private static void testPseudoTable() {
        System.out.println("=== Test 1: Pseudo Table (DUAL) ===");

        String oracleSql = "SELECT 1 FROM DUAL";
        String snowflakeSql = "SELECT 1";

        executeAndPrint(oracleSql, snowflakeSql);
    }

    /**
     * 测试 2：日期 / 时间函数
     */
    private static void testDateFunction() {
        System.out.println("\n=== Test 2: Date Function ===");

        String oracleSql = "SELECT SYSDATE FROM DUAL";
        String snowflakeSql = "SELECT CURRENT_TIMESTAMP()";

        executeAndPrint(oracleSql, snowflakeSql);
    }

    /**
     * 测试 3：空字符串行为
     */
    private static void testEmptyStringBehavior() {
        System.out.println("\n=== Test 3: Empty String vs NULL ===");

        String oracleSql =
                "SELECT CASE WHEN '' IS NULL THEN 'EMPTY STRING IS NULL' ELSE 'NOT NULL' END FROM DUAL";

        String snowflakeSql =
                "SELECT CASE WHEN '' IS NULL THEN 'EMPTY STRING IS NULL' ELSE 'NOT NULL' END";

        executeAndPrint(oracleSql, snowflakeSql);
    }

    /**
     * 测试 4：大小写敏感性
     */
    private static void testCaseSensitivity() {
        System.out.println("\n=== Test 4: Case Sensitivity ===");

        String oracleSql =
                "SELECT employee_id, first_name FROM employees WHERE ROWNUM = 1";

        String snowflakeSql =
                "SELECT \"EMPLOYEE_ID\", \"FIRST_NAME\" FROM EMPLOYEES LIMIT 1";

        executeAndPrint(oracleSql, snowflakeSql);
    }

    /**
     * 公共执行方法：打印 SQL + 打印结果
     */
    private static void executeAndPrint(String oracleSql, String snowflakeSql) {
        try (
                Connection oracleConn =
                        DriverManager.getConnection(ORACLE_URL, ORACLE_USER, ORACLE_PASSWORD);
                Connection snowflakeConn =
                        DriverManager.getConnection(SNOWFLAKE_URL, SNOWFLAKE_USER, SNOWFLAKE_PASSWORD)
        ) {
            System.out.println("\n[Oracle SQL]");
            System.out.println("  " + oracleSql);

            Statement oracleStmt = oracleConn.createStatement();
            ResultSet oracleRs = oracleStmt.executeQuery(oracleSql);
            printResultSet("Oracle Result", oracleRs);

            System.out.println("\n[Snowflake SQL]");
            System.out.println("  " + snowflakeSql);

            Statement snowflakeStmt = snowflakeConn.createStatement();
            ResultSet snowflakeRs = snowflakeStmt.executeQuery(snowflakeSql);
            printResultSet("Snowflake Result", snowflakeRs);

            oracleRs.close();
            snowflakeRs.close();
            oracleStmt.close();
            snowflakeStmt.close();

        } catch (SQLException e) {
            System.out.println("❌ Error: " + e.getMessage());
        }
    }

    /**
     * 打印 ResultSet（通用）
     */
    private static void printResultSet(String label, ResultSet rs) throws SQLException {
        ResultSetMetaData meta = rs.getMetaData();
        int columnCount = meta.getColumnCount();

        System.out.println("[" + label + "]");
        while (rs.next()) {
            StringBuilder row = new StringBuilder("  ");
            for (int i = 1; i <= columnCount; i++) {
                row.append(rs.getString(i)).append(" | ");
            }
            System.out.println(row);
        }
    }
}
