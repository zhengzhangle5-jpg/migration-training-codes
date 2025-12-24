package com.migration.day08.bug1;

import javax.sql.DataSource;

public class Bug1Main {

    public static void main(String[] args) {

        System.out.println("=== Start Bug 1 (Connection Timeout) Demo ===");

        DataSource dataSource =
                SnowflakeDataSourceFactory.createBuggyDataSource();

        Bug1ConnectionTimeoutDemo demo =
                new Bug1ConnectionTimeoutDemo(dataSource);

        demo.run();
    }
}

