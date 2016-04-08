package com.mishu.cgwy.accounting.controller;

import com.mishu.cgwy.response.query.QueryResponse;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class PaymentQueryResponse<PaymentVo> extends QueryResponse {
    private BigDecimal totalAmount = BigDecimal.ZERO;

}
