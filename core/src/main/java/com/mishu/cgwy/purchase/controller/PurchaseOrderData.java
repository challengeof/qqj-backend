package com.mishu.cgwy.purchase.controller;

import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;
import java.util.List;

@Data
public class PurchaseOrderData {

    private Long id;

    private Long cityId;

    private Long organizationId;

    private Long vendorId;

    private Long depotId;

    @DateTimeFormat(pattern="yyyy-MM-dd")
    private Date expectedArrivedDate;

    private String remark;

    private List<Long> cutOrders;

    private List<PurchaseOrderItemData> purchaseOrderItems;

    private Boolean approvalResult;

    private String opinion;

    private Short type;
}
