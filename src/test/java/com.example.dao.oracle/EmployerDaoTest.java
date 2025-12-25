package com.example.dao.oracle;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

import java.sql.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EmployerDaoTest {


    @Mock
    Connection connection;

    @Mock
    PreparedStatement ps;

    @Mock
    ResultSet rs;

    EmployerDao dao;

    @BeforeEach
    void setUp() throws Exception {
//        MockitoAnnotations.openMocks(this);
        dao = new EmployerDao(connection);
        when(connection.prepareStatement(anyString()))
                .thenReturn(ps);
//        dao = new EmployerDao(connection);
    }

    @Test
    @DisplayName("TC-001: 正常查询，返回id=2的记录")
    void testFindById_success() throws Exception {

        // 1️⃣ 假装 SQL 查到了数据
        when(ps.executeQuery()).thenReturn(rs);
        when(rs.next()).thenReturn(true);

        when(rs.getInt("EMPLOYER_ID")).thenReturn(2);
        when(rs.getString("EMPLOYER_NAME")).thenReturn("Green Foods");
        when(rs.getString("INDUSTRY")).thenReturn("Finance");
        when(rs.getInt("EMP_COUNT")).thenReturn(120);
        when(rs.getTimestamp("CREATED_DATE"))
                .thenReturn(Timestamp.valueOf("2018-03-15 00:00:00"));


        // 2️⃣ 调用被测方法
        Employer employer = dao.findById(2);

        // 3️⃣ 验证结果
        assertNotNull(employer);
        assertEquals(2, employer.getId());
        assertEquals("Green Foods", employer.getName());
        assertEquals("Finance", employer.getIndustry());
        assertEquals(120, employer.getEmpCount());
    }

    @Test
    @DisplayName("TC-010: 查询不存在的 employerId，返回 null")
    void testFindById_notExist() throws Exception {

        // Given: SQL 正常执行，但没有数据
        when(ps.executeQuery()).thenReturn(rs);
        when(rs.next()).thenReturn(false);

        // When
        Employer employer = dao.findById(99);

        // Then
        assertNull(employer, "当查询不到记录时，应返回 null");
    }

    @Test
    @DisplayName("TC-011: ResultSet 为空时返回 null")
    void testFindById_emptyResultSet() throws Exception {

        // Given: ResultSet 为空
        when(ps.executeQuery()).thenReturn(rs);
        when(rs.next()).thenReturn(false);

        // When
        Employer employer = dao.findById(1);

        // Then
        assertNull(employer, "ResultSet 为空时，应返回 null");
    }

    @Test
    @DisplayName("TC-012: SQL 异常时向上抛出 SQLException")
    void testFindById_sqlException() throws Exception {

        // Given: 执行 SQL 时抛出异常
        when(ps.executeQuery())
                .thenThrow(new SQLException("DB error"));

        // When & Then
        SQLException ex = assertThrows(
                SQLException.class,
                () -> dao.findById(1),
                "发生 SQL 异常时，应向上抛出 SQLException"
        );

        // 可选：校验异常信息
        assertEquals("DB error", ex.getMessage());
    }

}
