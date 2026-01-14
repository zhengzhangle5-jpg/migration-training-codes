package com.migration.miniProject.data_Verify;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

/**
 * Day14 CSV 初始化启动类（目录规范版）
 */
public class miniProduct_CsvDataInitRunner {

    private static final String[] TABLES = {
            "ORDERS",
            "ORDER_ITEMS",
            "PRODUCTS",
            "CATEGORIES",
            "CUSTOMERS"
    };

    private static final String BASE_DIR = "data/miniProject";
    private static final String ORACLE_DIR = BASE_DIR + File.separator + "oracle";
    private static final String SNOWFLAKE_DIR = BASE_DIR + File.separator + "snowflake";

    public static void main(String[] args) {

        System.out.println("===== Day14 CSV 初始化开始 =====");

        try {
            createDirIfNotExists(ORACLE_DIR);
            createDirIfNotExists(SNOWFLAKE_DIR);

            for (String table : TABLES) {

                // ===== Oracle =====
                OracleTableExporter.exportTable(table, 0);
                moveCsvFile(
                        table + "-all.csv",
                        ORACLE_DIR + File.separator + table + "-all.csv"
                );

                // ===== Snowflake =====
                SnowflakeTableExporter.exportTable(table, 0);
                moveCsvFile(
                        table + "-all.csv",
                        SNOWFLAKE_DIR + File.separator + table + "-all.csv"
                );
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        System.out.println("===== Day14 CSV 初始化完成 =====");
    }

    private static void createDirIfNotExists(String dirPath) throws Exception {
        File dir = new File(dirPath);
        if (!dir.exists()) {
            Files.createDirectories(dir.toPath());
        }
    }

    private static void moveCsvFile(String sourceFile, String targetFile) throws Exception {
        Path source = new File(sourceFile).toPath();
        Path target = new File(targetFile).toPath();
        Files.move(source, target, StandardCopyOption.REPLACE_EXISTING);
    }
}
