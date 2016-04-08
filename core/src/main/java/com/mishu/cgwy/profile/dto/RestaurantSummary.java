package com.mishu.cgwy.profile.dto;

import lombok.Data;

import java.math.BigDecimal;

/**
 * Created by bowen on 15-5-12.
 */
@Data
public class RestaurantSummary {

    private int restaurantCount;

    private BigDecimal totalConsumption;

    private BigDecimal monthlyConsumption;

    private Long aliveCustomer;

}
