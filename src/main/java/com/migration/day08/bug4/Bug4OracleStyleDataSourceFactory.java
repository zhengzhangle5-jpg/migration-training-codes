package com.migration.day08.bug4;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import javax.sql.DataSource;
import java.sql.Connection;

public class Bug4OracleStyleDataSourceFactory {

    public static DataSource createDataSource() {

        HikariConfig config = new HikariConfig();
        config.setPoolName("bug4-oracle-style");

        config.setDriverClassName("net.snowflake.client.jdbc.SnowflakeDriver");
        config.setJdbcUrl("jdbc:snowflake://PCNZPCZ-QB93968.snowflakecomputing.com/");

        config.addDataSourceProperty("user", "zzl");
        config.addDataSourceProperty("password", "20030828zzlZzl");
        config.addDataSourceProperty("db", "MIGRATION_TRAINING");
        config.addDataSourceProperty("schema", "PRACTICE");
        config.addDataSourceProperty("warehouse", "COMPUTE_WH");
        config.addDataSourceProperty("role", "ACCOUNTADMIN");

        // ❌ Oracle 风格事务配置（在 Snowflake 中无意义）
        config.setAutoCommit(false);
        config.setTransactionIsolation("TRANSACTION_SERIALIZABLE");

        return new HikariDataSource(config);
    }
}

