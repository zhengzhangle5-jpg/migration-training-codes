package com.migration.tools.ddlconverter;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class OracleTableMetadataReader {

    public List<ColumnMeta> readTableColumns(Connection conn, String tableName)
            throws SQLException {

        List<ColumnMeta> columns = new ArrayList<>();

        DatabaseMetaData metaData = conn.getMetaData();
        ResultSet rs = metaData.getColumns(
                null,
                conn.getSchema(),
                tableName.toUpperCase(),
                null
        );

        while (rs.next()) {
            columns.add(new ColumnMeta(
                    rs.getString("COLUMN_NAME"),
                    rs.getString("TYPE_NAME"),
                    rs.getInt("COLUMN_SIZE"),
                    rs.getInt("DECIMAL_DIGITS")
            ));
        }
        return columns;
    }
}



