package com.migration.day08.bug2;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class Bug2StatementLeakService {

    private final DataSource dataSource;

    public Bug2StatementLeakService(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public void execute(int taskId) {

        try {
            Connection conn = dataSource.getConnection();

            PreparedStatement ps =
                    conn.prepareStatement(
                            "SELECT CURRENT_TIMESTAMP()"
                    );

            ResultSet rs = ps.executeQuery();
            rs.next();

            System.out.println(
                    "[Task-" + taskId + "] result = " + rs.getString(1)
            );

            // ❌ Bug 2：彻底不释放任何 JDBC 资源
            // conn / ps / rs 全部不关

            // 🔥 拉长占用时间，确保超过泄漏阈值
            Thread.sleep(60_000);

            conn.close();

        } catch (Exception e) {
            System.err.println(
                    "[Task-" + taskId + "] ERROR: " + e.getMessage()
            );
        }
    }
}
