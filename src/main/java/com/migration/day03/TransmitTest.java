package com.migration.day03;
//test

import java.sql.*;

public class TransmitTest {

    // Oracle
    private static final String ORACLE_URL =
            "jdbc:oracle:thin:@//192.168.0.120:1521/XEPDB1";
    private static final String ORACLE_USER = "migration_user";
    private static final String ORACLE_PASSWORD = "MigrationPass123";

    // Snowflake
    private static final String SNOWFLAKE_URL =
            "jdbc:snowflake://PCNZPCZ-QB93968.snowflakecomputing.com/?" +
                    "db=MIGRATION_TRAINING&schema=PRACTICE&warehouse=COMPUTE_WH";
    private static final String SNOWFLAKE_USER = "zzl";
    private static final String SNOWFLAKE_PASSWORD = "20030828zzlZzl";

    public static void main(String[] args) throws Exception {

        try (
                Connection oracleConn =
                        DriverManager.getConnection(
                                ORACLE_URL, ORACLE_USER, ORACLE_PASSWORD);

                Connection snowflakeConn =
                        DriverManager.getConnection(
                                SNOWFLAKE_URL, SNOWFLAKE_USER, SNOWFLAKE_PASSWORD)
        ) {
            migrateOrders(oracleConn, snowflakeConn);
        }
    }

    private static void migrateOrders(
            Connection oracleConn,
            Connection snowflakeConn
    ) throws Exception {

        String selectSql = """
            SELECT
                ORDER_ID,
                USER_ID,
                PRICE,
                QUANTITY,
                TOTAL_AMOUNT,
                ORDER_TIME,
                STATUS,
                CREATED_AT
            FROM ORDERS
            ORDER BY ORDER_ID
        """;

        String insertSql = """
            INSERT INTO ORDERS (
                ORDER_ID,
                USER_ID,
                PRICE,
                QUANTITY,
                TOTAL_AMOUNT,
                ORDER_TIME,
                STATUS,
                CREATED_AT
            ) VALUES (?, ?, ?, ?, ?, ?, ?, ?)
        """;

        try (
                Statement stmt = oracleConn.createStatement();
                ResultSet rs = stmt.executeQuery(selectSql);
                PreparedStatement ps =
                        snowflakeConn.prepareStatement(insertSql)
        ) {
            int count = 0;

            while (rs.next()) {
                ps.setLong(1, rs.getLong("ORDER_ID"));
                ps.setLong(2, rs.getLong("USER_ID"));

                // 🔥 精度问题就在这里“悄悄发生”
                ps.setBigDecimal(3, rs.getBigDecimal("PRICE"));
                ps.setInt(4, rs.getInt("QUANTITY"));
                ps.setBigDecimal(5, rs.getBigDecimal("TOTAL_AMOUNT"));

                ps.setTimestamp(6, rs.getTimestamp("ORDER_TIME"));
                ps.setString(7, rs.getString("STATUS"));
                ps.setTimestamp(8, rs.getTimestamp("CREATED_AT"));

                ps.executeUpdate();
                count++;
            }

            System.out.println("✅ Migrated rows: " + count);
        }
    }
}
