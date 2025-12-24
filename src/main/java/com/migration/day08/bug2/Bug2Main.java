package com.migration.day08.bug2;

import javax.sql.DataSource;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Bug2Main {

    public static void main(String[] args) {

        System.out.println("=== Start Bug 2 (Statement/ResultSet Leak) ===");

        DataSource dataSource =
                SafeSnowflakeDataSourceFactory.createDataSource();

        Bug2StatementLeakService service =
                new Bug2StatementLeakService(dataSource);

        ExecutorService executor =
                Executors.newFixedThreadPool(5);

        for (int i = 1; i <= 5; i++) {
            int taskId = i;
            executor.submit(() -> service.execute(taskId));
        }

        executor.shutdown();
    }
}

