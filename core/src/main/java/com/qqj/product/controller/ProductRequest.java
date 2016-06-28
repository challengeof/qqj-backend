package com.qqj.product.controller;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class ProductRequest {

    private String name;
    private Short status;
    private BigDecimal price;
    private BigDecimal price0;
    private BigDecimal price1;
    private BigDecimal price2;

}
