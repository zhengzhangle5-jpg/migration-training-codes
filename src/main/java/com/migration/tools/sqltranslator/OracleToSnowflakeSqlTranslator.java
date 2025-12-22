package com.migration.tools.sqltranslator;

/**
 * Day 6 - SQL Translator
 */
public class OracleToSnowflakeSqlTranslator {

    //用于转换sql查询语句，暂时不用
    public String translate(String oracleSql) {
        String sql = oracleSql;

        sql = sql.replaceAll("NVL\\(", "COALESCE(");
        sql = sql.replaceAll("SYSDATE", "CURRENT_TIMESTAMP");
        sql = sql.replaceAll("FROM DUAL", "");

        return sql;
    }
}

