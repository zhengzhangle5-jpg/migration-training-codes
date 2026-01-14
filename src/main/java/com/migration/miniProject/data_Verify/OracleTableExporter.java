package com.migration.miniProject.data_Verify;

import com.migration.tools.common.OracleConnectionUtil;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.sql.*;

public class OracleTableExporter {

    public static void exportTable(String tableName, int sampleSize) throws Exception {

        try (Connection conn = OracleConnectionUtil.getConnection()) {

            String schema = conn.getSchema();
            DatabaseMetaData meta = conn.getMetaData();

            ResultSet cols = meta.getColumns(
                    null,
                    schema,
                    tableName.toUpperCase(),
                    null
            );

            StringBuilder select = new StringBuilder("SELECT ");
            boolean first = true;

            while (cols.next()) {
                String colName = cols.getString("COLUMN_NAME");
                int dataType = cols.getInt("DATA_TYPE");

                if (!first) select.append(", ");
                first = false;

                /* ========= 日期类型处理 ========= */
                if (dataType == Types.DATE) {
                    // DATE 类型去掉时分秒，仅保留日期
                    select.append("TO_CHAR(")
                            .append(colName)
                            .append(", 'YYYY-MM-DD') AS ")
                            .append(colName);

                } else if (dataType == Types.TIMESTAMP
                        || dataType == Types.TIMESTAMP_WITH_TIMEZONE) {
                    // TIMESTAMP 保留完整时间
                    select.append("TO_CHAR(")
                            .append(colName)
                            .append(", 'YYYY-MM-DD HH24:MI:SS') AS ")
                            .append(colName);

                } else {
                    select.append(colName);
                }
            }

            select.append(" FROM ").append(tableName);

            if (sampleSize > 0) {
                // 随机取样
                select.append(" ORDER BY DBMS_RANDOM.VALUE FETCH FIRST ")
                        .append(sampleSize)
                        .append(" ROWS ONLY");
            }

            String sql = select.toString();
            System.out.println("Executing SQL:\n" + sql);

            try (PreparedStatement ps = conn.prepareStatement(sql);
                 ResultSet rs = ps.executeQuery()) {

                writeCsv(rs,
                        tableName + "-" +
                                (sampleSize <= 0 ? "all" : sampleSize) + ".csv");
            }
        }
    }

    private static void writeCsv(ResultSet rs, String file) throws Exception {

        try (BufferedWriter w = new BufferedWriter(new FileWriter(file))) {

            ResultSetMetaData m = rs.getMetaData();
            int c = m.getColumnCount();

            // 写表头
            for (int i = 1; i <= c; i++) {
                w.write(m.getColumnLabel(i));
                if (i < c) w.write(",");
            }
            w.newLine();

            // 写数据
            while (rs.next()) {
                for (int i = 1; i <= c; i++) {
                    String v = rs.getString(i);
                    // NULL 值处理
                    if (v == null || v.trim().isEmpty()) {
                        w.write("NULL");
                    } else {
                        // 如果值中包含逗号或引号，加双引号包裹并转义内部引号
                        if (v.contains(",") || v.contains("\"") || v.contains("\n")) {
                            v = "\"" + v.replace("\"", "\"\"") + "\"";
                        }
                        w.write(v);
                    }
                    if (i < c) w.write(",");
                }
                w.newLine();
            }
        }
    }
}
