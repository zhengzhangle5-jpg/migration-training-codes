package com.migration.day08.bug3;

import javax.sql.DataSource;

public class Bug3Main {

    public static void main(String[] args) {

        System.out.println("=== Bug 3: Oracle Params in Snowflake ===");

        // ❌ Oracle 风格（有安全假象）
        DataSource oracleStyleDs =
                Bug3OracleStyleDataSourceFactory.createDataSource();

        new Bug3Service(oracleStyleDs).run();

        System.out.println("---- Switch to Snowflake Safe Config ----");

        // ✅ Snowflake 正确方式
        DataSource safeDs =
                SnowflakeSecureDataSourceFactory.create();

        new Bug3Service(safeDs).run();

        System.out.println("=== Bug 3 Finished ===");
    }
}
