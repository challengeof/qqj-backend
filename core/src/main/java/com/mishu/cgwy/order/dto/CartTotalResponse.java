package com.mishu.cgwy.order.dto;

import com.mishu.cgwy.error.RestError;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

@Data
@EqualsAndHashCode(callSuper = true)
public class CartTotalResponse extends RestError {
    private int total;
    private int type;
    private BigDecimal money = BigDecimal.ZERO;
}
