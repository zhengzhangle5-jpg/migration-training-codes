package com.migration.day08.bug2;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import javax.sql.DataSource;

public class SafeSnowflakeDataSourceFactory {

    public static DataSource createDataSource() {

        HikariConfig config = new HikariConfig();

        config.setDriverClassName(
                "net.snowflake.client.jdbc.SnowflakeDriver"
        );
        config.setJdbcUrl(
                "jdbc:snowflake://PCNZPCZ-QB93968.snowflakecomputing.com/"
        );
        config.setUsername("zzl");
        config.setPassword("20030828zzlZzl");

        // === Snowflake 属性 ===
        config.addDataSourceProperty("db", "MIGRATION_TRAINING");
        config.addDataSourceProperty("schema", "PRACTICE");
        config.addDataSourceProperty("warehouse", "COMPUTE_WH");
        config.addDataSourceProperty("role", "ACCOUNTADMIN");
        config.addDataSourceProperty("CLIENT_SESSION_KEEP_ALIVE", "true");

        // === 已验证的安全参数 ===
        config.setPoolName("snowflake-hikari-bug2");
        config.setMaximumPoolSize(3);
        config.setMinimumIdle(1);
        config.setConnectionTimeout(60_000);
        config.setIdleTimeout(120_000);
        config.setMaxLifetime(900_000);

        // 🔥 Bug 2 关键：开启泄漏检测
        config.setLeakDetectionThreshold(5_000);

        config.setConnectionTestQuery("SELECT 1");

        return new HikariDataSource(config);
    }
}
