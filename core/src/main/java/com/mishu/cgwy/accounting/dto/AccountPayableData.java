package com.mishu.cgwy.accounting.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Data
public class AccountPayableData {
    private Long id;

    private BigDecimal currentWriteOffAmount;

    private Date writeOffDate;
}
