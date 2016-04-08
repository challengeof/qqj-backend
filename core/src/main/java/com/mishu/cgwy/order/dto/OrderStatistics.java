package com.mishu.cgwy.order.dto;

import lombok.Data;

import java.math.BigDecimal;

/**
 * Created by bowen on 15-5-12.
 */
@Data
public class OrderStatistics {

    private BigDecimal total;

    private long firstOrderCount;

    private long restaurantCount;
}
