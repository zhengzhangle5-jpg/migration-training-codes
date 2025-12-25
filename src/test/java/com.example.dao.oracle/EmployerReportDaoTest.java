package com.example.dao.oracle;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.sql.*;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EmployerReportDaoTest {

    @Mock
    Connection connection;

    @Mock
    PreparedStatement ps;

    @Mock
    ResultSet rs;

    EmployerReportDao dao;

    @BeforeEach
    void setUp() throws Exception {
        dao = new EmployerReportDao(connection);
        when(connection.prepareStatement(anyString()))
                .thenReturn(ps);
    }

    @Test
    @DisplayName("TC-021: 按行业统计 - 正常场景（含 DECODE / 排序）")
    void testStatByIndustry_normal() throws Exception {

        // Given
        when(connection.prepareStatement(anyString()))
                .thenReturn(ps);
        when(ps.executeQuery()).thenReturn(rs);

        // 3 行数据
        when(rs.next()).thenReturn(true, true, true, false);

        // 第 1 行（FIN）
        when(rs.getString("INDUSTRY"))
                .thenReturn("FIN", "Technology", "Healthcare");
        when(rs.getInt("EMPLOYER_CNT"))
                .thenReturn(2, 1, 1);
        when(rs.getInt("TOTAL_EMP"))
                .thenReturn(700, 350, 260);
        when(rs.getBigDecimal("TOTAL_SALARY"))
                .thenReturn(
                        new BigDecimal("11000000"),
                        new BigDecimal("5000000"),
                        new BigDecimal("2000000")
                );

        EmployerReportDao dao = new EmployerReportDao(connection);

        // When
        List<IndustryReport> result = dao.statByIndustry(2023);

        // Then
        assertEquals(3, result.size());

        IndustryReport r1 = result.get(0);
        assertEquals("FIN", r1.getIndustry());
        assertEquals(2, r1.getEmployerCount());
        assertEquals(700, r1.getTotalEmp());
        assertEquals(new BigDecimal("11000000"), r1.getTotalSalary());

        IndustryReport r2 = result.get(1);
        assertEquals("Technology", r2.getIndustry());

        IndustryReport r3 = result.get(2);
        assertEquals("Healthcare", r3.getIndustry());
    }


    @Test
    @DisplayName("TC-030: 没有统计数据时返回空 List")
    void testStatByIndustry_emptyResult() throws Exception {

        // Given: 查询成功，但无任何结果
        when(ps.executeQuery()).thenReturn(rs);
        when(rs.next()).thenReturn(false);

        // When
        List<IndustryReport> result = dao.statByIndustry(1999);

        // Then
        assertNotNull(result, "返回值不应为 null");
        assertTrue(result.isEmpty(), "无数据时应返回空 List");
    }

    @Test
    @DisplayName("TC-031: NVL 生效 - NULL 聚合字段处理")
    void testStatByIndustry_nullAggregateFields() throws Exception {

        when(connection.prepareStatement(anyString()))
                .thenReturn(ps);
        when(ps.executeQuery()).thenReturn(rs);
        when(rs.next()).thenReturn(true, false);

        when(rs.getString("INDUSTRY")).thenReturn("FIN");
        when(rs.getInt("EMPLOYER_CNT")).thenReturn(1);
        when(rs.getInt("TOTAL_EMP")).thenReturn(0);
        when(rs.getBigDecimal("TOTAL_SALARY"))
                .thenReturn(BigDecimal.ZERO);

        EmployerReportDao dao = new EmployerReportDao(connection);

        List<IndustryReport> result = dao.statByIndustry(2023);

        IndustryReport r = result.get(0);
        assertEquals(0, r.getTotalEmp());
        assertEquals(BigDecimal.ZERO, r.getTotalSalary());
    }


    @Test
    @DisplayName("TC-032: SQL 异常时向上抛出 SQLException")
    void testStatByIndustry_sqlException() throws Exception {

        // Given: 执行 SQL 时直接抛异常
        when(ps.executeQuery())
                .thenThrow(new SQLException("DB error"));

        // When & Then
        SQLException ex = assertThrows(
                SQLException.class,
                () -> dao.statByIndustry(2023),
                "SQL 异常应向上抛出"
        );

        assertEquals("DB error", ex.getMessage());
    }


}


