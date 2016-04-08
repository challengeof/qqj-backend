package com.mishu.cgwy.product.controller;

import com.mishu.cgwy.response.query.QueryResponse;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Setter
@Getter
public class SkuPriceHistoryQueryResponse<AccountPayable> extends QueryResponse {
    private String[] labels;
    private Object[] data;
}
