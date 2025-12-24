package com.migration.day08.bug3;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import javax.sql.DataSource;
import java.util.Properties;

public class Bug3OracleStyleDataSourceFactory {

    public static DataSource createDataSource() {

        HikariConfig config = new HikariConfig();

        config.setPoolName("bug3-oracle-style");
        config.setMaximumPoolSize(3);
        config.setMinimumIdle(1);
        config.setConnectionTimeout(30000);

        config.setJdbcUrl(
                "jdbc:snowflake://PCNZPCZ-QB93968.snowflakecomputing.com/"
        );
        config.setDriverClassName(
                "net.snowflake.client.jdbc.SnowflakeDriver"
        );

        Properties props = new Properties();

        // ===== Oracle ONLY 参数（在 Snowflake 中完全无效）=====
        props.setProperty("oracle.net.encryption_client", "REQUIRED");
        props.setProperty("oracle.net.crypto_checksum_client", "REQUIRED");
        props.setProperty("oracle.net.encryption_types_client", "(AES256)");

        // ===== Snowflake 正常参数 =====
        props.setProperty("user", "zzl");
        props.setProperty("password", "20030828zzlZzl");
        props.setProperty("db", "MIGRATION_TRAINING");
        props.setProperty("schema", "PRACTICE");
        props.setProperty("warehouse", "COMPUTE_WH");
        props.setProperty("role", "ACCOUNTADMIN");

        config.setDataSourceProperties(props);

        return new HikariDataSource(config);
    }
}

