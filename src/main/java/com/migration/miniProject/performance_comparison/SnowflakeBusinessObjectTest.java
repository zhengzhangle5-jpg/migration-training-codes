package com.migration.miniProject.performance_comparison;

import com.migration.tools.common.SnowflakeConnectionUtil;

import java.sql.*;

public class SnowflakeBusinessObjectTest {

    public static void main(String[] args) {

        try (Connection conn = SnowflakeConnectionUtil.getConnection()) {

            System.out.println("✅ Snowflake connection successful\n");

            testCustomerOrderSummaryView(conn);
            testTopCustomersView(conn);
            testCalculateOrderTotal(conn);
            testGenerateInvoice(conn);
            testProcessRefund(conn);
            testGetDiscountRate(conn);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // =========================
    // 1. CUSTOMER_ORDER_SUMMARY
    // =========================
    private static void testCustomerOrderSummaryView(Connection conn) throws SQLException {
        System.out.println("==== CUSTOMER_ORDER_SUMMARY ====");

        String sql = "SELECT * FROM CUSTOMER_ORDER_SUMMARY ORDER BY CUSTOMER_ID";

        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                System.out.printf(
                        "CustomerID=%d | Name=%s | Orders=%d | Total=%.2f | Avg=%.2f | Last=%s%n",
                        rs.getInt("CUSTOMER_ID"),
                        rs.getString("CUSTOMER_NAME"),
                        rs.getInt("TOTAL_ORDERS"),
                        rs.getDouble("TOTAL_ORDER_AMOUNT"),
                        rs.getDouble("AVG_ORDER_AMOUNT"),
                        rs.getDate("LAST_ORDER_DATE")
                );
            }
        }
        System.out.println();
    }

    // =========================
    // 2. TOP_CUSTOMERS
    // =========================
    private static void testTopCustomersView(Connection conn) throws SQLException {
        System.out.println("==== TOP_CUSTOMERS ====");

        String sql = "SELECT * FROM TOP_CUSTOMERS ORDER BY LIFETIME_VALUE DESC";

        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                System.out.printf(
                        "CustomerID=%d | Name=%s | LifetimeValue=%.2f%n",
                        rs.getInt("CUSTOMER_ID"),
                        rs.getString("CUSTOMER_NAME"),
                        rs.getDouble("LIFETIME_VALUE")
                );
            }
        }
        System.out.println();
    }

    // =========================
    // 3. calculate_order_total
    // =========================
    private static void testCalculateOrderTotal(Connection conn) throws SQLException {
        System.out.println("==== calculate_order_total ====");

        String sql = "CALL calculate_order_total(?)";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, 1);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    // Snowflake：RETURN 值在第 1 列
                    double total = rs.getDouble(1);
                    System.out.println("OrderID=1 | Total=" + total);
                }
            }
        }
        System.out.println();
    }


    // =========================
    // 4. generate_invoice
    // =========================
    private static void testGenerateInvoice(Connection conn) throws SQLException {
        System.out.println("==== generate_invoice ====");

        String sql = "CALL generate_invoice(?)";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, 1);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    // Snowflake：返回值在第一列，列名叫 RESULT
                    System.out.println(rs.getString(1));
                }
            }
        }
        System.out.println();
    }


    // =========================
    // 5. process_refund
    // =========================
    private static void testProcessRefund(Connection conn) throws SQLException {
        System.out.println("==== process_refund ====");

        String sql = "CALL process_refund(?)";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, 1);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    System.out.println(rs.getString(1));
                }
            }
        }
        System.out.println();
    }


    // =========================
    // 6. get_discount_rate
    // =========================
    private static void testGetDiscountRate(Connection conn) throws SQLException {
        System.out.println("==== get_discount_rate ====");

        String sql = "SELECT get_discount_rate(?)";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, "BUSINESS");
            try (ResultSet rs = ps.executeQuery()) {
                rs.next();
                System.out.println("BUSINESS=" + rs.getDouble(1));
            }

            ps.setString(1, "RETAIL");
            try (ResultSet rs = ps.executeQuery()) {
                rs.next();
                System.out.println("RETAIL=" + rs.getDouble(1));
            }

            ps.setString(1, "OTHER");
            try (ResultSet rs = ps.executeQuery()) {
                rs.next();
                System.out.println("OTHER=" + rs.getDouble(1));
            }
        }
        System.out.println();
    }
}
