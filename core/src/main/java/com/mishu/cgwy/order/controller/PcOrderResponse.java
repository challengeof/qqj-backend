package com.mishu.cgwy.order.controller;

import lombok.Data;

import java.math.BigDecimal;

/**
 * Created by apple on 15/8/17.
 */
@Data
public class PcOrderResponse {

    private Long id;
    private String name;
    private String phone;
    private BigDecimal totalMoney;
}
