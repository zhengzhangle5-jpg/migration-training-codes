package com.migration.day08.bug1;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import javax.sql.DataSource;

public class SnowflakeDataSourceFactory {

    public static DataSource createBuggyDataSource() {

        HikariConfig config = new HikariConfig();

        // === Snowflake JDBC 基本配置 ===
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

        // === ❌ Bug 1：危险的连接池配置 ===
        config.setPoolName("snowflake-hikari");

        config.setMaximumPoolSize(5);        // 池子太小
        config.setMinimumIdle(5);             // 假活连接
        config.setConnectionTimeout(30_000);  // 30s 超时
        config.setIdleTimeout(600_000);       // 空闲过长
        config.setMaxLifetime(1_800_000);     // 生命周期过长

        // ❌ 关闭泄漏检测
        config.setLeakDetectionThreshold(0);

        return new HikariDataSource(config);
    }

    public static DataSource createSafeDataSource() {

        HikariConfig config = new HikariConfig();

        // === Snowflake JDBC 基本配置 ===
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

        // === ✅ 修复后的 HikariCP 参数 ===
        config.setPoolName("snowflake-hikari-safe");

        config.setMaximumPoolSize(10);       // 合理并发上限
        config.setMinimumIdle(1);            // 少量空闲
        config.setConnectionTimeout(60_000); // 60s 等待
        config.setIdleTimeout(120_000);      // 2 分钟释放
        config.setMaxLifetime(900_000);      // 15 分钟重建

        // ✅ 必须开启泄漏检测
        config.setLeakDetectionThreshold(30_000);

        return new HikariDataSource(config);
    }
}

