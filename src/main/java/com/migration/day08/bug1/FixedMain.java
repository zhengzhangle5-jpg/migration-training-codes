package com.migration.day08.bug1;

import javax.sql.DataSource;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class FixedMain {

    public static void main(String[] args) {

        DataSource ds =
                SnowflakeDataSourceFactory.createSafeDataSource();

        SafeQueryService service =
                new SafeQueryService(ds);

        ExecutorService executor =
                Executors.newFixedThreadPool(10);

        for (int i = 1; i <= 10; i++) {
            int taskId = i;
            executor.submit(() -> service.execute(taskId));
        }

        executor.shutdown();
    }
}

