package com.migration.tools.common;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.sql.*;

/**
 * 通用 CSV 写工具（原始文本版，迁移 / 对账推荐）
 */
public class CsvWriterUtil {

    public static void writeResultSet(ResultSet rs, String filePath) throws Exception {

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {

            ResultSetMetaData meta = rs.getMetaData();
            int colCount = meta.getColumnCount();

            /* ---------- 表头 ---------- */
            for (int i = 1; i <= colCount; i++) {
                writer.write(meta.getColumnName(i));
                if (i < colCount) writer.write(",");
            }
            writer.newLine();

            /* ---------- 数据 ---------- */
            while (rs.next()) {
                for (int i = 1; i <= colCount; i++) {

                    int type = meta.getColumnType(i);
                    String out;

                    if (rs.getObject(i) == null) {
                        out = "NULL";

                    } else if (type == Types.CLOB || type == Types.NCLOB) {
                        Clob clob = rs.getClob(i);
                        out = clob == null
                                ? "NULL"
                                : clob.getSubString(1, (int) clob.length());

                    } else if (type == Types.DATE) {
                        // ✅ DATE 类型仅保留日期
                        Date date = rs.getDate(i);
                        out = (date == null ? "NULL" : date.toString()); // YYYY-MM-DD

                    } else if (type == Types.TIMESTAMP
                            || type == Types.TIMESTAMP_WITH_TIMEZONE) {
                        // ✅ TIMESTAMP 保留完整时间
                        Timestamp ts = rs.getTimestamp(i);
                        out = (ts == null ? "NULL" : ts.toString().substring(0, 19)); // YYYY-MM-DD HH:MM:SS

                    } else {
                        // 其他类型使用 JDBC 字符串
                        out = rs.getString(i);
                    }

                    writer.write(escapeCsv(out));
                    if (i < colCount) writer.write(",");
                }
                writer.newLine();
            }
        }
    }


    /** CSV 转义（RFC4180） */
    private static String escapeCsv(String value) {
        if (value == null) return "NULL";

        boolean needQuote =
                value.contains(",") ||
                        value.contains("\"") ||
                        value.contains("\n") ||
                        value.contains("\r");

        if (!needQuote) return value;

        return "\"" + value.replace("\"", "\"\"") + "\"";
    }
}
