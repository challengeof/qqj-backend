package com.mishu.cgwy.accounting.controller;

import com.mishu.cgwy.response.query.QueryResponse;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class AccountPayableWriteOffQueryResponse<AccountPayableWriteOff> extends QueryResponse {
    private BigDecimal totalWriteOffAmount = BigDecimal.ZERO;
}
