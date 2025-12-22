package com.migration.tools.dataloader;

import java.sql.*;

public class SnowflakeDataInserter {

    public void insert(ResultSet rs, Connection snowflakeConn, String table)
            throws SQLException {

        ResultSetMetaData meta = rs.getMetaData();
        int colCount = meta.getColumnCount();

        String placeholders = "?,".repeat(colCount);
        placeholders = placeholders.substring(0, placeholders.length() - 1);

        String sql = "INSERT INTO " + table + " VALUES (" + placeholders + ")";
        PreparedStatement ps = snowflakeConn.prepareStatement(sql);

        while (rs.next()) {
            for (int i = 1; i <= colCount; i++) {
                ps.setObject(i, rs.getObject(i));
            }
            ps.executeUpdate();
        }
    }
}

