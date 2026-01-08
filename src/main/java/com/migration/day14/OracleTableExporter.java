package com.migration.day14;

import com.migration.tools.common.OracleConnectionUtil;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.sql.*;
import java.time.LocalDate;
import java.time.ZoneId;

/**
 * Oracle 表数据导出工具（DATE 字段仅保留日期）
 */
public class OracleTableExporter {

    /**
     * @param tableName 表名
     * @param sampleSize <=0 表示全表，否则随机抽样
     */
    public static void exportTable(String tableName, int sampleSize) throws Exception {

        String sql;
        String suffix;

        if (sampleSize <= 0) {
            sql = "SELECT * FROM " + tableName;
            suffix = "all";
        } else {
            sql = "SELECT * FROM " + tableName +
                    " ORDER BY DBMS_RANDOM.VALUE FETCH FIRST ? ROWS ONLY";
            suffix = String.valueOf(sampleSize);
        }

        try (Connection conn = OracleConnectionUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            if (sampleSize > 0) {
                ps.setInt(1, sampleSize);
            }

            ResultSet rs = ps.executeQuery();
            writeResultSetToCsv(rs, tableName + "-" + suffix + ".csv");
            System.out.println("Oracle 导出完成: " + tableName + "-" + suffix + ".csv");
        }
    }

    /**
     * 写 CSV（对 Oracle DATE / TIMESTAMP 做日期标准化）
     */
    private static void writeResultSetToCsv(ResultSet rs, String filePath) throws Exception {

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {

            ResultSetMetaData meta = rs.getMetaData();
            int colCount = meta.getColumnCount();

            // 表头
            for (int i = 1; i <= colCount; i++) {
                writer.write(meta.getColumnName(i));
                if (i < colCount) writer.write(",");
            }
            writer.newLine();

            // 数据
            while (rs.next()) {
                for (int i = 1; i <= colCount; i++) {

                    Object value = rs.getObject(i);
                    String output;

                    if (value == null) {
                        output = "NULL";

                    } else if (value instanceof Timestamp) {
                        // Oracle DATE / TIMESTAMP → 只保留日期
                        LocalDate date = ((Timestamp) value)
                                .toInstant()
                                .atZone(ZoneId.systemDefault())
                                .toLocalDate();
                        output = date.toString();

                    } else if (value instanceof Date) {
                        LocalDate date = ((Date) value).toLocalDate();
                        output = date.toString();

                    } else {
                        output = value.toString();
                    }

                    writer.write(output);
                    if (i < colCount) writer.write(",");
                }
                writer.newLine();
            }
        }
    }
}
