package com.migration.miniProject.performance_comparison;

import com.migration.tools.common.OracleConnectionUtil;

import java.sql.*;

public class OracleBusinessObjectTest {

    public static void main(String[] args) {

        try (Connection conn = OracleConnectionUtil.getConnection()) {

            System.out.println("✅ Oracle connection successful\n");

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

    private static void testCustomerOrderSummaryView(Connection conn) throws SQLException {
        System.out.println("==== CUSTOMER_ORDER_SUMMARY ====");

        String sql = "SELECT * FROM CUSTOMER_ORDER_SUMMARY ORDER BY customer_id";

        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                System.out.printf(
                        "CustomerID=%d | Name=%s | Orders=%d | Total=%.2f | Avg=%.2f | Last=%s%n",
                        rs.getInt("customer_id"),
                        rs.getString("customer_name"),
                        rs.getInt("total_orders"),
                        rs.getDouble("total_order_amount"),
                        rs.getDouble("avg_order_amount"),
                        rs.getDate("last_order_date")
                );
            }
        }
        System.out.println();
    }

    private static void testTopCustomersView(Connection conn) throws SQLException {
        System.out.println("==== TOP_CUSTOMERS ====");

        String sql = "SELECT * FROM TOP_CUSTOMERS ORDER BY lifetime_value DESC";

        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                System.out.printf(
                        "CustomerID=%d | Name=%s | LifetimeValue=%.2f%n",
                        rs.getInt("customer_id"),
                        rs.getString("customer_name"),
                        rs.getDouble("lifetime_value")
                );
            }
        }
        System.out.println();
    }

    private static void testCalculateOrderTotal(Connection conn) throws SQLException {
        System.out.println("==== calculate_order_total ====");

        String sql = "{ call calculate_order_total(?, ?) }";

        try (CallableStatement cs = conn.prepareCall(sql)) {
            cs.setInt(1, 1);
            cs.registerOutParameter(2, Types.NUMERIC);
            cs.execute();

            System.out.println("OrderID=1 | Total=" + cs.getDouble(2));
        }
        System.out.println();
    }

    private static void testGenerateInvoice(Connection conn) throws SQLException {
        System.out.println("==== generate_invoice ====");

        String sql = "{ call generate_invoice(?, ?) }";

        try (CallableStatement cs = conn.prepareCall(sql)) {
            cs.setInt(1, 1);
            cs.registerOutParameter(2, Types.VARCHAR);
            cs.execute();

            System.out.println(cs.getString(2));
        }
        System.out.println();
    }

    private static void testProcessRefund(Connection conn) throws SQLException {
        System.out.println("==== process_refund ====");

        String sql = "{ call process_refund(?, ?) }";

        try (CallableStatement cs = conn.prepareCall(sql)) {
            cs.setInt(1, 1);
            cs.registerOutParameter(2, Types.VARCHAR);
            cs.execute();

            System.out.println("Result: " + cs.getString(2));
        }
        System.out.println();
    }

    private static void testGetDiscountRate(Connection conn) throws SQLException {
        System.out.println("==== get_discount_rate ====");

        String sql = "SELECT get_discount_rate(?) FROM dual";

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
