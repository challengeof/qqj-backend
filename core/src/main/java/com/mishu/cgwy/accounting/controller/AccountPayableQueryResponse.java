package com.mishu.cgwy.accounting.controller;

import com.mishu.cgwy.response.Response;
import com.mishu.cgwy.response.query.QueryResponse;
import lombok.Data;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Data
public class AccountPayableQueryResponse<AccountPayable> extends QueryResponse {

    private BigDecimal totalAmount = BigDecimal.ZERO;

    private BigDecimal totalWriteOffAmount = BigDecimal.ZERO;

    private BigDecimal totalUnWriteOffAmount = BigDecimal.ZERO;

}
