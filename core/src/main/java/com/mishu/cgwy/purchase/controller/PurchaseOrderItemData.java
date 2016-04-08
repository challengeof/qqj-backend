package com.mishu.cgwy.purchase.controller;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * Created by wangguodong on 15/9/16.
 */
@Data
public class PurchaseOrderItemData {

    private Long id;

    private Long skuId;

    private Integer purchaseQuantity;

    private Integer needQuantity;

    private BigDecimal purchasePrice;

    private BigDecimal rate;

    private Long vendorId;

    private List<Long> purchaseOrderItemIds;
}
