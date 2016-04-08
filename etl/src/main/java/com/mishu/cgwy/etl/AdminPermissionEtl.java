package com.mishu.cgwy.etl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;

/**
 * Created by kaicheng on 3/31/15.
 */
@Service
public class AdminPermissionEtl {
    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private JdbcTemplate legacyJdbcTemplate;

    @Transactional
    public void transfer() {
        List<Object[]> batchArgs = Arrays.asList(
                new Object[]{81, "update-order", "订单编辑"},
                new Object[]{80, "update-restaurant", "餐馆编辑"},
                new Object[]{79, "vendor-list", "供货商管理"},
                new Object[]{78, "agent-list", "代理商管理"},
                new Object[]{77, "zone-list", "商圈管理"},
                new Object[]{75, "agent-task-list", "代理商任务列表"},
                new Object[]{74, "order-group-list", "车辆调度列表"},
                new Object[]{73, "finance-list", "财务列表"},
                new Object[]{72, "dynamic-price-list", "商品价格管理"},
                new Object[]{71, "sku-list", "sku管理"},
                new Object[]{70, "product-list", "商品管理"},
                new Object[]{69, "category-list", "分类管理"},
                new Object[]{68, "order-detail-list", "sku销量详情"},
                new Object[]{67, "order-list", "订单列表"},
                new Object[]{66, "restaurant-list", "餐馆列表"},
                new Object[]{65, "assign-customer-service", "分配销售客服"},
                new Object[]{64, "admin-list", "人员管理"});
        jdbcTemplate.batchUpdate("insert into admin_permission(id, name, display_name) values( ? , ? , ?)", batchArgs);

    }
}
