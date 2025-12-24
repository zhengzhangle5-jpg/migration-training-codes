package com.migration.day08.bug2;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

public class CorrectBug2Service {

    private final DataSource dataSource;

    public CorrectBug2Service(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public void execute() {
        try (
                Connection conn = dataSource.getConnection();
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery("SELECT CURRENT_TIMESTAMP")
        ) {
            if (rs.next()) {
                System.out.println(
                        "[" + Thread.currentThread().getName() + "] result = " +
                                rs.getString(1)
                );
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
