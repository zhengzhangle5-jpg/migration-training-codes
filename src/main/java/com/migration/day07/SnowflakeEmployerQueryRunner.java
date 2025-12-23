package com.migration.day07;

import com.example.dao.snowflake.Employer;
import com.example.dao.snowflake.EmployerDao;
import com.migration.tools.common.SnowflakeConnectionUtil;

import java.sql.Connection;

public class SnowflakeEmployerQueryRunner {

    public static void main(String[] args) throws Exception {

        int employerId = 2;

        try (Connection conn =
                     SnowflakeConnectionUtil.getConnection()) {

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
