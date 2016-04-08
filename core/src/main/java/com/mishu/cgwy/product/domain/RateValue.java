package com.mishu.cgwy.product.domain;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.math.BigDecimal;

/**
 * Created by wangwei on 15/9/16.
 */
@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum RateValue {

    A(BigDecimal.valueOf(0)), B(BigDecimal.valueOf(0.13)), C(BigDecimal.valueOf(0.17));

    private BigDecimal value;

    public BigDecimal getValue() {
        return value;
    }

    private RateValue(BigDecimal value) {
        this.value = value;
    }
}
