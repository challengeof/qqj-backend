package com.mishu.cgwy.purchase.controller;

import com.mishu.cgwy.response.query.QueryResponse;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class PurchaseAccordingResultResponse<PurchaseOrderItemWrapper> extends QueryResponse {
    private BigDecimal totalAmount;
}
