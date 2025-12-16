package com.migration;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

public class EnvironmentTest {
    public static void main(String[] args) {
        // 测试 Snowflake 连接
        System.out.println("=== Testing Snowflake Connection ===");
        testSnowflake();

        // 测试 Oracle 连接
        System.out.println("\n=== Testing Oracle Connection ===");
        testOracle();
    }

    private static void testSnowflake() {
        String url = "jdbc:snowflake://PCNZPCZ-QB93968.snowflakecomputing.com/?db=MIGRATION_TRAINING&schema=PRACTICE&warehouse=COMPUTE_WH";
        String user = "zzl";
        String password = "20030828zzlZzl";

        try (Connection conn = DriverManager.getConnection(url, user, password);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT CURRENT_VERSION()")) {
            if (rs.next()) {
                System.out.println("✅ Snowflake connection successful!");
                System.out.println("   Version: " + rs.getString(1));
            }
        } catch (Exception e) {
            System.out.println("❌ Snowflake connection failed: " + e.getMessage());
        }
    }

    private static void testOracle() {
        String url = "jdbc:oracle:thin:@//192.168.0.120:1521/XEPDB1";
        String user = "migration_user";
        String password = "MigrationPass123";

        try (Connection conn = DriverManager.getConnection(url, user, password);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT 'Oracle OK' FROM dual")) {

            if (rs.next()) {
                System.out.println("✅ Oracle connection successful!");
                System.out.println("   " + rs.getString(1));
            }
        } catch (Exception e) {
            System.out.println("❌ Oracle connection failed: " + e.getMessage());
        }
    }

}
