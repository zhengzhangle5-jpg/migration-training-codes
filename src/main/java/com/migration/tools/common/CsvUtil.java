package com.migration.tools.common;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;

/**
 * 通用 CSV 写工具
 */
public class CsvUtil {

    public static void writeResultSet(ResultSet rs, String filePath) throws Exception {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
            ResultSetMetaData meta = rs.getMetaData();
            int columnCount = meta.getColumnCount();

            // 写表头
            for (int i = 1; i <= columnCount; i++) {
                writer.write(meta.getColumnName(i));
                if (i < columnCount) {
                    writer.write(",");
                }
            }
            writer.newLine();

            // 写数据
            while (rs.next()) {
                for (int i = 1; i <= columnCount; i++) {
                    Object value = rs.getObject(i);
                    writer.write(value == null ? "NULL" : value.toString());
                    if (i < columnCount) {
                        writer.write(",");
                    }
                }
                writer.newLine();
            }
        }
    }
}
