package com.mishu.cgwy.purchase.controller;

import lombok.Data;
import org.springframework.data.domain.PageRequest;
import org.springframework.format.annotation.DateTimeFormat;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Data
public class PurchaseOrderRequest {

    private Long cityId;

    private Long organizationId;

    private Long vendorId;

    private Long paymentVendorId;

    private Long depotId;

    private Long productId;

    private Long skuId;

    private String productName;

    private Long id;

    private Long cutOrderId;

    private Short status;

    private Boolean print;

    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm")
    private Date startDate;

    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm")
    private Date endDate;

    private List<Long> cutOrders;

    private PageRequest pageRequest;

    private int page = 0;

    private int pageSize = 100;

    private Short type;

    private BigDecimal minAmount;

    private BigDecimal maxAmount;

    private BigDecimal minPaymentAmount;

    private BigDecimal maxPaymentAmount;

    private Short sign;
}
