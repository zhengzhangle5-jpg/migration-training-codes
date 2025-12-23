package com.migration.day07;

import com.example.dao.oracle.IndustryReport;
import com.example.dao.oracle.EmployerReportDao;
import com.migration.tools.common.OracleConnectionUtil;

import java.sql.Connection;
import java.util.List;

public class OracleEmployerReportRunner {

    public static void main(String[] args) throws Exception {

        int reportYear = 2023;

        try (Connection conn =
                     OracleConnectionUtil.getConnection()) {

            EmployerReportDao dao =
                    new EmployerReportDao(conn);

            List<IndustryReport> reports =
                    dao.statByIndustry(reportYear);

            System.out.println(
                    "=== Oracle Industry Report (" + reportYear + ") ===");

            for (IndustryReport r : reports) {
                System.out.println(
                        "Industry=" + r.getIndustry()
                                + ", Employers=" + r.getEmployerCount()
                                + ", TotalEmp=" + r.getTotalEmp()
                                + ", TotalSalary=" + r.getTotalSalary()
                );
            }
        }
    }
}

