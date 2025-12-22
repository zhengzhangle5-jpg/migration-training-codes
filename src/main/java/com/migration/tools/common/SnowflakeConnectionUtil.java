package com.migration.tools.common;

import java.sql.Connection;
import java.sql.DriverManager;
import java.util.Properties;

public class SnowflakeConnectionUtil {

    public static Connection getConnection() throws Exception {
        String url = "jdbc:snowflake://PCNZPCZ-QB93968.snowflakecomputing.com/?db=MIGRATION_TRAINING&schema=PRACTICE&warehouse=COMPUTE_WH";
        String user = "zzl";
        String password = "20030828zzlZzl";

        return DriverManager.getConnection(url, user, password);
    }
}

