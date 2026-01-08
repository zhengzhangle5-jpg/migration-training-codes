package com.migration.day14;

import com.migration.tools.common.CsvReaderUtil;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class UsersTableValidationTest {

    private static final String ORACLE_CSV =
            "data/oracle/USERS-all.csv";
    private static final String SNOWFLAKE_CSV =
            "data/snowflake/USERS-all.csv";

    /* =========================
       Level 1：行数验证
       ========================= */
    @Test
    void level1_rowCount_shouldMatch() throws Exception {

        List<String> oracleRows =
                CsvReaderUtil.readDataLines(ORACLE_CSV);
        List<String> snowflakeRows =
                CsvReaderUtil.readDataLines(SNOWFLAKE_CSV);

        Assertions.assertEquals(
                oracleRows.size(),
                snowflakeRows.size(),
                "Oracle 与 Snowflake 行数不一致"
        );
    }

    /* =========================
       Level 2：列和值校验（Hash）
       ========================= */
    @Test
    void level2_checksum_shouldMatch() throws Exception {

        long oracleChecksum = calculateChecksum(ORACLE_CSV);
        long snowflakeChecksum = calculateChecksum(SNOWFLAKE_CSV);

        Assertions.assertEquals(
                oracleChecksum,
                snowflakeChecksum,
                "Oracle 与 Snowflake 校验和不一致"
        );
    }

    private long calculateChecksum(String csvPath) throws Exception {

        MessageDigest md = MessageDigest.getInstance("MD5");
        List<String> rows = CsvReaderUtil.readDataLines(csvPath);

        for (String row : rows) {
            md.update(row.getBytes(StandardCharsets.UTF_8));
        }

        byte[] digest = md.digest();

        long checksum = 0;
        for (byte b : digest) {
            checksum += (b & 0xff);
        }
        return checksum;
    }

    /* =========================
       Level 4：全量 Diff
       ========================= */
    @Test
    void level4_fullDataDiff_shouldBeEmpty() throws Exception {

        Set<String> oracleSet =
                new HashSet<>(CsvReaderUtil.readDataLines(ORACLE_CSV));
        Set<String> snowflakeSet =
                new HashSet<>(CsvReaderUtil.readDataLines(SNOWFLAKE_CSV));

        Set<String> oracleMinusSnowflake = new HashSet<>(oracleSet);
        oracleMinusSnowflake.removeAll(snowflakeSet);

        Set<String> snowflakeMinusOracle = new HashSet<>(snowflakeSet);
        snowflakeMinusOracle.removeAll(oracleSet);

        Assertions.assertTrue(
                oracleMinusSnowflake.isEmpty()
                        && snowflakeMinusOracle.isEmpty(),
                () -> buildDiffMessage(
                        oracleMinusSnowflake,
                        snowflakeMinusOracle)
        );
    }

    private String buildDiffMessage(
            Set<String> oracleOnly,
            Set<String> snowflakeOnly) {

        StringBuilder sb = new StringBuilder("\n=== 数据差异 ===\n");

        if (!oracleOnly.isEmpty()) {
            sb.append("仅存在 Oracle 的记录:\n");
            oracleOnly.forEach(r -> sb.append(r).append("\n"));
        }

        if (!snowflakeOnly.isEmpty()) {
            sb.append("仅存在 Snowflake 的记录:\n");
            snowflakeOnly.forEach(r -> sb.append(r).append("\n"));
        }

        return sb.toString();
    }
}
