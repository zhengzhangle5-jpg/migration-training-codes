package com.migration.day07;

import com.example.dao.oracle.Employer;
import com.example.dao.oracle.EmployerDao;
import com.migration.tools.common.OracleConnectionUtil;

import java.sql.Connection;

public class OracleEmployerQueryRunner {

    public static void main(String[] args) throws Exception {

        int employerId = 2;

        try (Connection conn =
                     OracleConnectionUtil.getConnection()) {

            EmployerDao dao = new EmployerDao(conn);
            Employer employer = dao.findById(employerId);

            if (employer == null) {
                System.out.println("No employer found, id=" + employerId);
            } else {
                System.out.println("=== Oracle Employer Result ===");
                System.out.println("ID        : " + employer.getId());
                System.out.println("Name      : " + employer.getName());
                System.out.println("Industry  : " + employer.getIndustry());
                System.out.println("Emp Count : " + employer.getEmpCount());
                System.out.println("Created   : " + employer.getCreatedDate());
            }
        }
    }
}
