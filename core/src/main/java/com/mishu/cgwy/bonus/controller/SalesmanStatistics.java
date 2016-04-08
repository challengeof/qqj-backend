package com.mishu.cgwy.bonus.controller;

import com.mishu.cgwy.admin.vo.AdminUserVo;
import lombok.Data;

/**
 * Created by bowen on 15/9/17.
 */
@Data
public class SalesmanStatistics {

    private AdminUserVo adminUser;

    private Integer orderQuantity = 0;

    private Integer deliveryQuantity = 0;

    private Integer refundQuantity = 0;

    private Integer newRestaurantQuantity = 0;

    private Integer visitQuantity = 0;

    private Integer visitDistinctRestaurantQuantity = 0;
}
