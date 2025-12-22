package com.migration.day06;

import com.migration.tools.common.*;
import com.migration.tools.dataexporter.*;
import com.migration.tools.dataloader.*;

import java.sql.Connection;
import java.sql.ResultSet;

public class OracleToSnowflakeMoveRecords {

    public static void main(String[] args) throws Exception {
        String table = "EMPLOYERS";

        Connection oracleConn = OracleConnectionUtil.getConnection();
        Connection snowflakeConn = SnowflakeConnectionUtil.getConnection();

        OracleDataReader reader = new OracleDataReader();
        ResultSet rs = reader.readAll(oracleConn, table);

        SnowflakeDataInserter inserter = new SnowflakeDataInserter();
        inserter.insert(rs, snowflakeConn, table);

        System.out.println("Data migration completed for table: " + table);
    }
}

