package com.migration.miniProject.data_Verify;

import com.migration.tools.common.CsvWriterUtil;
import com.migration.tools.common.SnowflakeConnectionUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

/**
 * Snowflake 表数据导出工具（终极版）
 *
 * 特点：
 * 1. DATE / TIMESTAMP 在 SQL 层转为 VARCHAR
 * 2. JDBC 不再参与时间语义（彻底修复 -8h / -9h）
 * 3. 适用于迁移、对账、CSV 比对
 */
public class SnowflakeTableExporter {

    /**
     * @param tableName  表名
     * @param sampleSize <=0 表示全表；>0 随机抽样
     */
    public static void exportTable(String tableName, int sampleSize) throws Exception {

        try (Connection conn = SnowflakeConnectionUtil.getConnection()) {

            // ⭐ 构造“安全的 SELECT”
            String baseSelectSql = buildSafeSelectSql(conn, tableName);

            String sql;
            String suffix;

            if (sampleSize <= 0) {
                sql = baseSelectSql;
                suffix = "all";
            } else {
                sql = baseSelectSql + " ORDER BY RANDOM() LIMIT ?";
                suffix = String.valueOf(sampleSize);
            }

            try (PreparedStatement ps = conn.prepareStatement(sql)) {

                if (sampleSize > 0) {
                    ps.setInt(1, sampleSize);
                }

                ResultSet rs = ps.executeQuery();
                String fileName = tableName + "-" + suffix + ".csv";

                CsvWriterUtil.writeResultSet(rs, fileName);
                System.out.println("Snowflake 导出完成: " + fileName);
            }
        }
    }

    /**
     * 构造一个“JDBC 安全”的 SELECT：
     * - DATE / TIMESTAMP → TO_VARCHAR
     * - 其他类型原样
     */
    private static String buildSafeSelectSql(Connection conn, String tableName) throws Exception {

        String metaSql = """
            SELECT COLUMN_NAME, DATA_TYPE
            FROM INFORMATION_SCHEMA.COLUMNS
            WHERE TABLE_SCHEMA = CURRENT_SCHEMA()
              AND TABLE_NAME = ?
            ORDER BY ORDINAL_POSITION
            """;

        StringBuilder select = new StringBuilder("SELECT ");

        try (PreparedStatement ps = conn.prepareStatement(metaSql)) {
            ps.setString(1, tableName.toUpperCase());
            ResultSet rs = ps.executeQuery();

            boolean first = true;
            while (rs.next()) {

                if (!first) {
                    select.append(", ");
                }
                first = false;

                String columnName = rs.getString("COLUMN_NAME");
                String dataType = rs.getString("DATA_TYPE");

                // ⭐ 关键：时间类型全部转字符串
                if (dataType.startsWith("TIMESTAMP")) {
                    select.append("TO_VARCHAR(")
                            .append(columnName)
                            .append(", 'YYYY-MM-DD HH24:MI:SS') AS ")
                            .append(columnName);

                } else if ("DATE".equals(dataType)) {
                    select.append("TO_VARCHAR(")
                            .append(columnName)
                            .append(", 'YYYY-MM-DD') AS ")
                            .append(columnName);

                } else {
                    select.append(columnName);
                }
            }
        }

        select.append(" FROM ").append(tableName);
        return select.toString();
    }
}
