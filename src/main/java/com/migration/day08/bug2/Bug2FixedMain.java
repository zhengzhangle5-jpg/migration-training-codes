package com.migration.day08.bug2;

import com.migration.day08.bug2.SafeSnowflakeDataSourceFactory;

import javax.sql.DataSource;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class Bug2FixedMain {

    public static void main(String[] args) throws Exception {

        System.out.println("=== Start Bug 2 FIXED (Safe Resource Management) ===");

        DataSource dataSource =
                SafeSnowflakeDataSourceFactory.createDataSource();

        ExecutorService executor = Executors.newFixedThreadPool(5);

        CorrectBug2Service service =
                new CorrectBug2Service(dataSource);

        for (int i = 1; i <= 5; i++) {
            executor.submit(service::execute);
        }

        executor.shutdown();
        executor.awaitTermination(1, TimeUnit.MINUTES);

        System.out.println("=== Bug 2 FIXED Finished ===");
    }
}
