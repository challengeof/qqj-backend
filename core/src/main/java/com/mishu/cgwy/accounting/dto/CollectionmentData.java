package com.mishu.cgwy.accounting.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class CollectionmentData {

    private Long collectionPaymentMethodId;

    private BigDecimal amount;
}
