package com.example.dao.snowflake;

import java.math.BigDecimal;

public class IndustryReport {

    private String industry;
    private int employerCount;
    private int totalEmp;
    private BigDecimal totalSalary;

    public String getIndustry() {
        return industry;
    }

    public void setIndustry(String industry) {
        this.industry = industry;
    }

    public int getEmployerCount() {
        return employerCount;
    }

    public void setEmployerCount(int employerCount) {
        this.employerCount = employerCount;
    }

    public int getTotalEmp() {
        return totalEmp;
    }

    public void setTotalEmp(int totalEmp) {
        this.totalEmp = totalEmp;
    }

    public BigDecimal getTotalSalary() {
        return totalSalary;
    }

    public void setTotalSalary(BigDecimal totalSalary) {
        this.totalSalary = totalSalary;
    }

    // getter / setter 省略
}
