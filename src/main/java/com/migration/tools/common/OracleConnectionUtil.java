package com.migration.tools.common;

import java.sql.Connection;
import java.sql.DriverManager;

public class OracleConnectionUtil {

    //返回一个数据库连接
    public static Connection getConnection() throws Exception {
        String url = "jdbc:oracle:thin:@//192.168.0.120:1521/XEPDB1";
        String user = "migration_user";
        String password = "MigrationPass123";

        return DriverManager.getConnection(url, user, password);
    }
}

