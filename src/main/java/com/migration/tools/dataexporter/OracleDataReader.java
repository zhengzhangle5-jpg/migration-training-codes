package com.migration.tools.dataexporter;

import java.sql.*;

public class OracleDataReader {

    public ResultSet readAll(Connection conn, String table) throws SQLException {
        Statement stmt = conn.createStatement(
                ResultSet.TYPE_FORWARD_ONLY,
                ResultSet.CONCUR_READ_ONLY
        );
        stmt.setFetchSize(500);
        return stmt.executeQuery("SELECT * FROM " + table);
    }
}

