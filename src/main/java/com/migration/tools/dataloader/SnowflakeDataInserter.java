package com.migration.tools.dataloader;

import java.sql.*;
import java.util.Calendar;
import java.util.TimeZone;

public class SnowflakeDataInserter {

    private static final int BATCH_SIZE = 2000;

    public void insert(ResultSet rs, Connection snowflakeConn, String table)
            throws SQLException {

        snowflakeConn.setAutoCommit(false); // ⭐ 1. 关闭自动提交

        ResultSetMetaData meta = rs.getMetaData();
        int colCount = meta.getColumnCount();

        String placeholders = "?,".repeat(colCount);
        placeholders = placeholders.substring(0, placeholders.length() - 1);

        String sql = "INSERT INTO " + table + " VALUES (" + placeholders + ")";
        PreparedStatement ps = snowflakeConn.prepareStatement(sql);

        Calendar utc = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
        int batchCount = 0;

        while (rs.next()) {

            for (int i = 1; i <= colCount; i++) {

                int type = meta.getColumnType(i);
                Object value = rs.getObject(i);

                if (type == Types.DATE || type == Types.TIMESTAMP) {
                    Timestamp ts = rs.getTimestamp(i, utc);
                    if (ts != null) {
                        ps.setString(i, ts.toString().replace('T', ' '));
                    } else {
                        ps.setNull(i, Types.TIMESTAMP);
                    }
                }
                else if (value instanceof Clob) {
                    Clob clob = (Clob) value;
                    ps.setString(i, clob.getSubString(1, (int) clob.length()));
                }
                else {
                    ps.setObject(i, value);
                }
            }

            ps.addBatch();           // ⭐ 2. 使用 batch
            batchCount++;

            if (batchCount % BATCH_SIZE == 0) {
                ps.executeBatch();   // ⭐ 3. 批量执行
                snowflakeConn.commit();
            }
        }

        ps.executeBatch();
        snowflakeConn.commit();

        ps.close();
        snowflakeConn.setAutoCommit(true);
    }
}