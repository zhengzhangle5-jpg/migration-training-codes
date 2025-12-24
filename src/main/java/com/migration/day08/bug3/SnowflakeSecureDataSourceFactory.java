package com.migration.day08.bug3;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import javax.sql.DataSource;
import java.util.Properties;

public class SnowflakeSecureDataSourceFactory {

    public static DataSource create() {

        HikariConfig config = new HikariConfig();

        // ======================
        // 基础 JDBC 配置
        // ======================
        config.setDriverClassName("net.snowflake.client.jdbc.SnowflakeDriver");
        config.setJdbcUrl("jdbc:snowflake://PCNZPCZ-QB93968.snowflakecomputing.com/");

        config.setMaximumPoolSize(3);
        config.setMinimumIdle(1);
        config.setPoolName("snowflake-secure-pool");

        // ======================
        // Snowflake 官方安全参数
        // ======================
        Properties props = new Properties();

        props.put("user", "zzl");
        props.put("password", "20030828zzlZzl");

        props.put("db", "MIGRATION_TRAINING");
        props.put("schema", "PRACTICE");
        props.put("warehouse", "COMPUTE_WH");
        props.put("role", "ACCOUNTADMIN");

        // ========= 安全关键 =========

        // 强制 TLS（即使是默认值，也建议显式）
        props.put("ssl", "on");

        // OCSP 校验策略
        // 生产环境建议 false（fail-close）
        props.put("ocspFailOpen", "false");

        // 避免连接频繁失效
        props.put("CLIENT_SESSION_KEEP_ALIVE", "true");

        // ======================
        // 可选：超时控制
        // ======================
        props.put("loginTimeout", "30");
        props.put("networkTimeout", "300");

        config.setDataSourceProperties(props);

        return new HikariDataSource(config);
    }
}

