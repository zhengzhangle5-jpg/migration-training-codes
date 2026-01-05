package com.migration.tools.dataloader;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.Calendar;
import java.util.TimeZone;

public class SnowflakeDataInserter {

    public void insert(ResultSet rs, Connection snowflakeConn, String table)
            throws SQLException {

        ResultSetMetaData meta = rs.getMetaData();
        int colCount = meta.getColumnCount();

        String placeholders = "?,".repeat(colCount);
        placeholders = placeholders.substring(0, placeholders.length() - 1);

        String sql = "INSERT INTO " + table + " VALUES (" + placeholders + ")";
        PreparedStatement ps = snowflakeConn.prepareStatement(sql);

        Calendar utc = Calendar.getInstance(TimeZone.getTimeZone("UTC"));

        while (rs.next()) {
            for (int i = 1; i <= colCount; i++) {

                int type = meta.getColumnType(i);

                if (type == Types.DATE || type == Types.TIMESTAMP) {
                    Timestamp ts = rs.getTimestamp(i, utc); // ⭐核心
                    //ps.setTimestamp(i, ts);
                    ps.setString(i, ts.toString().replace('T', ' '));
                } else {
                    ps.setObject(i, rs.getObject(i));
                }
            }
            ps.executeUpdate();
        }



    }
}

