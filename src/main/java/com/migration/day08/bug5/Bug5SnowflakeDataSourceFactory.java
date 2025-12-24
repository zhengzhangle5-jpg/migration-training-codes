package com.migration.day08.bug5;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import javax.sql.DataSource;
import java.util.Properties;

public class Bug5SnowflakeDataSourceFactory {

    public static DataSource createDataSource() {

        HikariConfig config = new HikariConfig();
        config.setPoolName("snowflake-pool");

        config.setDriverClassName("net.snowflake.client.jdbc.SnowflakeDriver");
        config.setJdbcUrl("jdbc:snowflake://PCNZPCZ-QB93968.snowflakecomputing.com/");

        // ✅ Snowflake 推荐
        config.setAutoCommit(true);
        // ❌ 不设置 transactionIsolation

        Properties props = new Properties();
        props.put("user", "zzl");
        props.put("password", "20030828zzlZzl");
        props.put("db", "MIGRATION_TRAINING");
        props.put("schema", "PRACTICE");
        props.put("warehouse", "COMPUTE_WH");
        props.put("role", "ACCOUNTADMIN");

        // ✅ Snowflake 安全参数
        props.put("ssl", "on");
        props.put("ocspFailOpen", "false");
        props.put("CLIENT_SESSION_KEEP_ALIVE", "true");

        config.setDataSourceProperties(props);

        return new HikariDataSource(config);
    }
}

