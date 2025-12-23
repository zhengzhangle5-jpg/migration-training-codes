package com.example.dao.snowflake;

import com.example.dao.snowflake.IndustryReport;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class EmployerReportDao {

    private final Connection connection;

    public EmployerReportDao(Connection connection) {
        this.connection = connection;
    }

    public List<IndustryReport> statByIndustry(int year)
            throws SQLException {

        String sql = """
    SELECT
        /* from Oracle DECODE(...) -> Snowflake CASE WHEN */
        CASE
            WHEN e.INDUSTRY = 'Finance' THEN 'FIN'
            WHEN e.INDUSTRY = 'IT' THEN 'TECH'
            ELSE e.INDUSTRY
        END AS INDUSTRY,

        COUNT(e.EMPLOYER_ID) AS EMPLOYER_CNT,

        /* from Oracle NVL(e.EMP_COUNT, 0) */
        SUM(COALESCE(e.EMP_COUNT, 0)) AS TOTAL_EMP,

        /* from Oracle NVL(s.TOTAL_SALARY, 0) */
        SUM(COALESCE(s.TOTAL_SALARY, 0)) AS TOTAL_SALARY
    FROM EMPLOYERS e
    JOIN EMPLOYER_SALARY s
      ON e.EMPLOYER_ID = s.EMPLOYER_ID
    WHERE s.REPORT_YEAR = ?
    GROUP BY
        CASE
            WHEN e.INDUSTRY = 'Finance' THEN 'FIN'
            WHEN e.INDUSTRY = 'IT' THEN 'TECH'
            ELSE e.INDUSTRY
        END
    ORDER BY TOTAL_SALARY DESC
""";


        try (PreparedStatement ps =
                     connection.prepareStatement(sql)) {

            ps.setInt(1, year);

            try (ResultSet rs = ps.executeQuery()) {

                List<IndustryReport> result = new ArrayList<>();

                while (rs.next()) {
                    IndustryReport r = new IndustryReport();
                    r.setIndustry(rs.getString("INDUSTRY"));
                    r.setEmployerCount(
                            rs.getInt("EMPLOYER_CNT"));
                    r.setTotalEmp(
                            rs.getInt("TOTAL_EMP"));
                    r.setTotalSalary(
                            rs.getBigDecimal("TOTAL_SALARY"));
                    result.add(r);
                }
                return result;
            }
        }
    }
}
