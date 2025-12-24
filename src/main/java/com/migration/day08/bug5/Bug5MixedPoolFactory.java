package com.migration.day08.bug5;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import javax.sql.DataSource;
import java.util.Properties;

public class Bug5MixedPoolFactory {

    /**
     * ❌ 错误示例：
     * 试图用一套“通用配置”同时支持 Oracle + Snowflake
     */
    public static DataSource createSnowflakeDataSource() {

        HikariConfig config = new HikariConfig();
        config.setPoolName("bug5-mixed-pool");

        // ❌ Snowflake Driver
        config.setDriverClassName("net.snowflake.client.jdbc.SnowflakeDriver");
        config.setJdbcUrl("jdbc:snowflake://PCNZPCZ-QB93968.snowflakecomputing.com/");

        // ❌ Oracle 风格事务配置（Snowflake 不支持）
        config.setAutoCommit(false);
        config.setTransactionIsolation("TRANSACTION_SERIALIZABLE");

        // ❌ Oracle 网络 / 安全参数（Snowflake 无效）
        Properties props = new Properties();
        props.put("user", "zzl");
        props.put("password", "20030828zzlZzl");
        props.put("db", "MIGRATION_TRAINING");
        props.put("schema", "PRACTICE");
        props.put("warehouse", "COMPUTE_WH");
        props.put("role", "ACCOUNTADMIN");

        // ❌ Oracle 专有参数
        props.put("oracle.net.encryption_client", "REQUIRED");
        props.put("oracle.net.crypto_checksum_client", "REQUIRED");

        config.setDataSourceProperties(props);

        return new HikariDataSource(config);
    }
}
