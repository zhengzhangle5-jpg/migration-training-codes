package com.migration.tools.common;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;

/**
 * CSV 读取工具（仅处理简单 CSV，无引号、无转义）
 */
public class CsvReaderUtil {

    /**
     * 读取 CSV 的数据行（不包含表头）
     */
    public static List<String> readDataLines(String filePath) throws Exception {
        return Files.readAllLines(Path.of(filePath))
                .stream()
                .skip(1) // 跳过表头
                .collect(Collectors.toList());
    }

    /**
     * 读取 CSV 的全部行（包含表头）
     */
    public static List<String> readAllLines(String filePath) throws Exception {
        return Files.readAllLines(Path.of(filePath));
    }
}
