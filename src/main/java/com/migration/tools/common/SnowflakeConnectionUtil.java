package com.migration.tools.common;

import java.sql.Connection;
import java.sql.DriverManager;
import java.util.Properties;

public class SnowflakeConnectionUtil {

    public static Connection getConnection() throws Exception {
        String url = "jdbc:snowflake://PCNZPCZ-QB93968.snowflakecomputing.com/?db=MIGRATION_TRAINING&schema=PRACTICE&warehouse=COMPUTE_WH";
        String user = "zzl";
        String password = "20030828zzlZzl";

        //snowsql -a PCNZPCZ-QB93968 -u zzl -r <role> -w <warehouse> -d <database> -s <schema>


        return DriverManager.getConnection(url, user, password);
    }

//    PUT file://C:/Users/pc005/Code/oracle-snowflake-migration-training/data/miniProject/oracle/CATEGORIES-all.csv @local_csv_stage auto_compress=false;
//    PUT file://C:/Users/pc005/Code/oracle-snowflake-migration-training/data/miniProject/oracle/CUSTOMERS-all.csv @local_csv_stage auto_compress=false;
//    PUT file://C:/Users/pc005/Code/oracle-snowflake-migration-training/data/miniProject/oracle/PRODUCTS-all.csv @local_csv_stage auto_compress=false;
//    PUT file://C:/Users/pc005/Code/oracle-snowflake-migration-training/data/miniProject/oracle/ORDERS-all.csv @local_csv_stage auto_compress=false;
//    PUT file://C:/Users/pc005/Code/oracle-snowflake-migration-training/data/miniProject/oracle/ORDER_ITEMS-all.csv @local_csv_stage auto_compress=false;

}

