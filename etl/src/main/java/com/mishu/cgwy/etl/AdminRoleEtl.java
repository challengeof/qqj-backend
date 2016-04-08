package com.mishu.cgwy.etl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * Created by kaicheng on 3/31/15.
 */
@Service
public class AdminRoleEtl {
    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private JdbcTemplate legacyJdbcTemplate;

    @Transactional
    public void transfer() {
        List<Object[]> batchArgs = Arrays.asList(
                new Object[]{14, "销售客服助理", "CustomerServiceAssistant"},
                new Object[]{13, "配送员助理", "LogisticsAssistant"},
                new Object[]{12, "配送员管理员", "LogisticsSupervisor"},
                new Object[]{11, "跟车员", "LogisticsStaff"},
                new Object[]{8, "运营员工", "OperationsStaff"},
                new Object[]{7, "运营主管", "OperationsSupervisor"},
                new Object[]{6, "财务", "FinancialStaff"},
                new Object[]{5, "采购员工", "PurchaseStaff"},
                new Object[]{4, "采购主管", "PurchaseSupervisor"},
                new Object[]{3, "销售客服员工", "CustomerService"},
                new Object[]{2, "销售客服主管", "CustomerServiceSupervisor"},
                new Object[]{1, "系统管理员", "Administrator"});
        jdbcTemplate.batchUpdate("insert into admin_role(id , display_name , name) values(? , ? , ?) ", batchArgs);

    }
}
