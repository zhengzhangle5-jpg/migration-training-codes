package com.example.bonus;

import com.migration.tools.common.SnowflakeConnectionUtil;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class BonusService {

    public void dynamicBonusUpdate(String tableName, BigDecimal rate) {

        String updateSql =
                "UPDATE " + tableName +
                        " SET bonus = salary * ?";

        try (Connection conn = SnowflakeConnectionUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(updateSql)) {

            ps.setBigDecimal(1, rate);
            ps.executeUpdate();

        } catch (Exception e) {
            logError(e.getMessage());
        }
    }

    private void logError(String errorMsg) {

        String logSql =
                "INSERT INTO proc_log (log_time, message) " +
                        "VALUES (CURRENT_TIMESTAMP, ?)";

        try (Connection conn = SnowflakeConnectionUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(logSql)) {

            ps.setString(1, errorMsg);
            ps.executeUpdate();

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}

