package com.migration.day08.bug1;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class SafeQueryService {

    private final DataSource dataSource;

    public SafeQueryService(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public void execute(int taskId) {

        // ✅ 连接只包住 JDBC 操作
        try (
                Connection conn = dataSource.getConnection();
                PreparedStatement ps =
                        conn.prepareStatement(
                                "SELECT CURRENT_TIMESTAMP()"
                        );
                ResultSet rs = ps.executeQuery()
        ) {

            rs.next();
            System.out.println(
                    "[Task-" + taskId + "] result: " + rs.getString(1)
            );

        } catch (Exception e) {
            System.err.println(
                    "[Task-" + taskId + "] ERROR: " + e.getMessage()
            );
        }

        // ✅ 业务耗时逻辑放在连接外
        doHeavyBusinessLogic(taskId);
    }

    private void doHeavyBusinessLogic(int taskId) {
        try {
            Thread.sleep(60_000);
            System.out.println(
                    "[Task-" + taskId + "] business done"
            );
        } catch (InterruptedException ignored) {
        }
    }
}
