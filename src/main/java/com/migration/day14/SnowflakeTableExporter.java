package com.migration.day14;

import com.migration.tools.common.CsvUtil;
import com.migration.tools.common.SnowflakeConnectionUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

/**
 * Snowflake 表数据导出工具
 */
public class SnowflakeTableExporter {

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
            sql = "SELECT * FROM " + tableName + " ORDER BY RANDOM() LIMIT ?";
            suffix = String.valueOf(sampleSize);
        }

        try (Connection conn = SnowflakeConnectionUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            if (sampleSize > 0) {
                ps.setInt(1, sampleSize);
            }

            ResultSet rs = ps.executeQuery();
            String fileName = tableName + "-" + suffix + ".csv";
            CsvUtil.writeResultSet(rs, fileName);

            System.out.println("Snowflake 导出完成: " + fileName);
        }
    }
}

