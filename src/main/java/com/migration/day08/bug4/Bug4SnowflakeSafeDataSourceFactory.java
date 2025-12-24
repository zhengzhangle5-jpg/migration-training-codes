package com.migration.day08.bug4;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import javax.sql.DataSource;

public class Bug4SnowflakeSafeDataSourceFactory {

    public static DataSource createDataSource() {

        HikariConfig config = new HikariConfig();
        config.setPoolName("bug4-snowflake-safe");

        config.setDriverClassName("net.snowflake.client.jdbc.SnowflakeDriver");
        config.setJdbcUrl("jdbc:snowflake://PCNZPCZ-QB93968.snowflakecomputing.com/");

        config.addDataSourceProperty("user", "zzl");
        config.addDataSourceProperty("password", "20030828zzlZzl");
        config.addDataSourceProperty("db", "MIGRATION_TRAINING");
        config.addDataSourceProperty("schema", "PRACTICE");
        config.addDataSourceProperty("warehouse", "COMPUTE_WH");
        config.addDataSourceProperty("role", "ACCOUNTADMIN");

        // ✅ Snowflake 推荐方式
        config.setAutoCommit(true);
        // ❗ 不设置 transactionIsolation

        return new HikariDataSource(config);
    }
}

