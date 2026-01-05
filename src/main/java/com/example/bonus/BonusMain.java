package com.example.bonus;

import com.example.bonus.BonusService;

import java.math.BigDecimal;

public class BonusMain {

    public static void main(String[] args) {

        BonusService service = new BonusService();

        // 等价 Oracle: CALL dynamic_bonus_update('EMPLOYEES', 0.1);
//        service.dynamicBonusUpdate("EMPLOYEES", new BigDecimal("0.1"));

        //测试失败情况
        service.dynamicBonusUpdate("EMPLOYEES_NOT_EXIST", new BigDecimal("0.1"));


        System.out.println("Bonus updated.");
    }
}

