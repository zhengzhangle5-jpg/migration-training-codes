package com.example.dao.oracle;

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
                /* Oracle 专用函数 DECODE，用于行业名称归一化 */
                DECODE(e.INDUSTRY,
                       'Finance', 'FIN',
                       'IT', 'TECH',
                       e.INDUSTRY) AS INDUSTRY,

                COUNT(e.EMPLOYER_ID) AS EMPLOYER_CNT,

                /* Oracle 专用函数 NVL，处理空值 */
                SUM(NVL(e.EMP_COUNT, 0)) AS TOTAL_EMP,

                SUM(NVL(s.TOTAL_SALARY, 0)) AS TOTAL_SALARY
            FROM EMPLOYERS e,
                 EMPLOYER_SALARY s
            WHERE e.EMPLOYER_ID = s.EMPLOYER_ID
              AND s.REPORT_YEAR = ?
            GROUP BY
                DECODE(e.INDUSTRY,
                       'Finance', 'FIN',
                       'IT', 'TECH',
                       e.INDUSTRY)
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
                    r.setEmployerCount(rs.getInt("EMPLOYER_CNT"));
                    r.setTotalEmp(rs.getInt("TOTAL_EMP"));
                    r.setTotalSalary(rs.getBigDecimal("TOTAL_SALARY"));
                    result.add(r);
                }
                return result;
            }
        }
    }
}
