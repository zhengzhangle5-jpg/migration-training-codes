package com.migration.day06;

import com.migration.tools.common.OracleConnectionUtil;
import com.migration.tools.ddlconverter.*;

import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.util.List;

public class OracleToSnowflakeMoveTable {

    public static void main(String[] args) throws Exception {
        String tableName = "EMPLOYERS";

        try (Connection conn = OracleConnectionUtil.getConnection()) {

            OracleTableMetadataReader reader =
                    new OracleTableMetadataReader();
            List<ColumnMeta> columns =
                    reader.readTableColumns(conn, tableName);

            SnowflakeDdlGenerator generator =
                    new SnowflakeDdlGenerator();
            String ddl = generator.generate(tableName, columns);

            Path output = Path.of("EMPLOYERS_snowflake.sql");
            Files.writeString(output, ddl);

            System.out.println("DDL generated at: " +
                    output.toAbsolutePath());
        }
    }
}

