package com.migration.day08.bug4;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

public class Bug4TransactionDemo {

    public static void run(Connection conn, String label) throws Exception {

        System.out.println("\n[" + label + "]");

        System.out.println("AutoCommit = " + conn.getAutoCommit());
        System.out.println("Isolation  = " + conn.getTransactionIsolation());

        try (Statement stmt = conn.createStatement()) {

            stmt.execute("CREATE OR REPLACE TEMP TABLE bug4_tx_test (id INT)");
            stmt.execute("INSERT INTO bug4_tx_test VALUES (1)");

            // 尝试提交
            conn.commit();

            ResultSet rs = stmt.executeQuery(
                    "SELECT COUNT(*) FROM bug4_tx_test");

            rs.next();
            System.out.println("Row count = " + rs.getInt(1));
        }
    }
}
