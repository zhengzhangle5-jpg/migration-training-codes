package com.migration.day08.bug1;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Bug1ConnectionTimeoutDemo {

    private final DataSource dataSource;

    public Bug1ConnectionTimeoutDemo(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public void run() {
        ExecutorService executor = Executors.newFixedThreadPool(10);

        for (int i = 1; i <= 10; i++) {
            int taskId = i;
            executor.submit(() -> executeSlowTask(taskId));
        }

        executor.shutdown();
    }

    private void executeSlowTask(int taskId) {
        try {
            System.out.println(
                    "[Task-" + taskId + "] trying to get connection..."
            );

            // ❌ 获取连接
            Connection conn = dataSource.getConnection();
            System.out.println(
                    "[Task-" + taskId + "] got connection"
            );

            // ❌ 模拟业务阻塞，占着连接
            Thread.sleep(60_000);

            PreparedStatement ps =
                    conn.prepareStatement("SELECT CURRENT_TIMESTAMP()");
            ResultSet rs = ps.executeQuery();
            rs.next();

            System.out.println(
                    "[Task-" + taskId + "] result: " + rs.getString(1)
            );

            // ❌ 故意不关闭连接
        } catch (Exception e) {
            System.err.println(
                    "[Task-" + taskId + "] ERROR: " + e.getMessage()
            );
            e.printStackTrace();
        }
    }
}
