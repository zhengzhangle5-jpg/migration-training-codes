package com.example.dao.oracle;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class EmployerDao {

    private final Connection connection;

    public EmployerDao(Connection connection) {
        this.connection = connection;
    }

    public Employer findById(int employerId) throws SQLException {

        String sql = """
            SELECT EMPLOYER_ID,
                   EMPLOYER_NAME,
                   INDUSTRY,
                   EMP_COUNT,
                   CREATED_DATE
            FROM EMPLOYERS
            WHERE EMPLOYER_ID = ?
        """;

        try (PreparedStatement ps =
                     connection.prepareStatement(sql)) {

            ps.setInt(1, employerId);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Employer employer = new Employer();
                    employer.setId(rs.getInt("EMPLOYER_ID"));
                    employer.setName(rs.getString("EMPLOYER_NAME"));
                    employer.setIndustry(rs.getString("INDUSTRY"));
                    employer.setEmpCount(rs.getInt("EMP_COUNT"));
                    employer.setCreatedDate(
                            rs.getTimestamp("CREATED_DATE")
                    );
                    return employer;
                }
            }
        }
        return null;
    }
}
