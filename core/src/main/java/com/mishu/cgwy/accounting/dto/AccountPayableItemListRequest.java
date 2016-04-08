package com.mishu.cgwy.accounting.dto;

import lombok.Data;
import org.springframework.data.domain.PageRequest;

import java.math.BigDecimal;
import java.util.Date;

@Data
public class AccountPayableItemListRequest {

    private Long cityId;

    private Long vendorId;

    private Short purchaseOrderType;

    private Short accountPayableType;

    private String productName;

    private Long purchaseOrderId;

    private Date purchaseOrderDateStart;

    private Date purchaseOrderDateEnd;

    private Date payableDateStart;

    private Date payableDateEnd;

    private PageRequest pageRequest;

    private int page = 0;

    private int pageSize = 100;
}
