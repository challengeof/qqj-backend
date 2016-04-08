package com.mishu.cgwy.response.query;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class QuerySummationResponse<T> extends QueryResponse<T> {

    private BigDecimal[] amount;
}
