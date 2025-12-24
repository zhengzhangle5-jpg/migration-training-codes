package com.migration.day08.bug3;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

public class Bug3Service {

    private final DataSource dataSource;

    public Bug3Service(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public void run() {
        try (
                Connection conn = dataSource.getConnection();
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(
                        "SELECT CURRENT_USER(), CURRENT_ROLE(), CURRENT_TIMESTAMP"
                )
        ) {
            while (rs.next()) {
                System.out.println(
                        "[RESULT] user=" + rs.getString(1) +
                                ", role=" + rs.getString(2) +
                                ", ts=" + rs.getString(3)
                );
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

