package com.migration.miniProject.trans_DDL;

import com.migration.tools.common.OracleConnectionUtil;
import com.migration.tools.ddlconverter.ColumnMeta;
import com.migration.tools.ddlconverter.OracleTableMetadataReader;
import com.migration.tools.ddlconverter.SnowflakeDdlGenerator;

import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.util.List;

public class miniProject_OracleToSnowflakeMoveTable {

    public static void main(String[] args) throws Exception {

        // ===== 1. Mini-Project 需要迁移的表 =====
        List<String> tableNames = List.of(
                "CUSTOMERS",
                "ORDERS",
                "ORDER_ITEMS",
                "PRODUCTS",
                "CATEGORIES"
        );

        try (Connection conn = OracleConnectionUtil.getConnection()) {

            OracleTableMetadataReader reader =
                    new OracleTableMetadataReader();

            SnowflakeDdlGenerator generator =
                    new SnowflakeDdlGenerator();

            for (String tableName : tableNames) {

                // ===== 2. 读取 Oracle 表结构 =====
                List<ColumnMeta> columns =
                        reader.readTableColumns(conn, tableName);

                // ===== 3. 生成 Snowflake DDL =====
                String ddl = generator.generate(tableName, columns);

                // ===== 4. 输出文件名：miniProject_XXX_snowflake.sql =====
                String fileName =
                        "miniProject_" + tableName + "_snowflake.sql";

                Path output = Path.of(fileName);
                Files.writeString(output, ddl);

                System.out.println(
                        "DDL generated for table " + tableName +
                                " at: " + output.toAbsolutePath()
                );
            }
        }
    }
}
