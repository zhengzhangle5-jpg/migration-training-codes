package com.migration.day08.bug5;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import javax.sql.DataSource;
import java.util.Properties;

public class Bug5OracleDataSourceFactory {

    public static DataSource createDataSource() {

        HikariConfig config = new HikariConfig();
        config.setPoolName("oracle-pool");

        config.setDriverClassName("oracle.jdbc.OracleDriver");
        config.setJdbcUrl("jdbc:oracle:thin:@//host:1521/service");

        config.setAutoCommit(false);
        config.setTransactionIsolation("TRANSACTION_READ_COMMITTED");

        Properties props = new Properties();
        props.put("user", "oracle_user");
        props.put("password", "oracle_pwd");

        // Oracle 专有参数
        props.put("oracle.net.encryption_client", "REQUIRED");
        props.put("oracle.net.crypto_checksum_client", "REQUIRED");

        config.setDataSourceProperties(props);

        return new HikariDataSource(config);
    }
}

