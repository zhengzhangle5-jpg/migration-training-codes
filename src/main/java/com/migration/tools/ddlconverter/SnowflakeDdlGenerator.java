package com.migration.tools.ddlconverter;

import java.util.List;

public class SnowflakeDdlGenerator {

    public String generate(String tableName, List<ColumnMeta> columns) {
        StringBuilder ddl = new StringBuilder();
        ddl.append("CREATE TABLE ").append(tableName).append(" (\n");

        for (int i = 0; i < columns.size(); i++) {
            ColumnMeta c = columns.get(i);
            ddl.append("  ")
                    .append(c.name())
                    .append(" ")
                    .append(mapType(c));

            if (i < columns.size() - 1) {
                ddl.append(",");
            }
            ddl.append("\n");
        }

        ddl.append(");");
        return ddl.toString();
    }

    private String mapType(ColumnMeta c) {
        return switch (c.type()) {

            //目前处理了varchar、number整数和小数精度的情况，data不完善，仅为了测试转换类功能
            //日后待改进部分：
            //date多种时间类型差异
            //oracle还有多种字符类型，CHAR,NCHAR,CLOB等
            //NOT NULL / DEFAULT / COMMENT 这些建表语句后的补充部分尚未解析
            //同样的，主键、索引等（这部分由人工操作）

            case "VARCHAR2" -> "VARCHAR(" + c.size() + ")";
            case "NUMBER" -> c.scale() > 0
                    ? "NUMBER(" + c.size() + "," + c.scale() + ")"
                    : "NUMBER(" + c.size() + ")";
            case "DATE" -> "TIMESTAMP_NTZ";
            default -> "VARCHAR";
        };
    }
}

