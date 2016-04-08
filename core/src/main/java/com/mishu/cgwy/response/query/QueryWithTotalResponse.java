package com.mishu.cgwy.response.query;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class QueryWithTotalResponse<T> extends QueryResponse<T> {

    private BigDecimal sum;
}
