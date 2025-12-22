package com.migration.tools.ddlconverter;

public record ColumnMeta(
        String name,
        String type,
        int size,

        //scale用于存小数位数
        int scale
) {}

