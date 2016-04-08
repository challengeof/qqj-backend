package com.mishu.cgwy.purchase.controller;

import lombok.Data;

import java.math.BigDecimal;

/**
 * Created by wangguodong on 15/10/10.
 */
@Data
public class ReturnNoteItemData {

    private Long id;

    private Integer returnQuantity;

    private BigDecimal returnPrice;

    private PurchaseOrderItemData purchaseOrderItem;
}
