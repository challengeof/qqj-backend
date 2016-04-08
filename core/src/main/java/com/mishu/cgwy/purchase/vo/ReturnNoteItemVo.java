package com.mishu.cgwy.purchase.vo;

import com.mishu.cgwy.purchase.domain.ReturnNoteItem;
import lombok.Data;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Data
public class ReturnNoteItemVo {

    private Long id;

    private Integer returnQuantity;

    private BigDecimal returnPrice;

    private BigDecimal returnBundleQuantity;

    private BigDecimal returnTotal;

    private PurchaseOrderItemVo purchaseOrderItem;

//    public ReturnNoteItemVo(ReturnNoteItem returnNoteItem) {
//        this.id = returnNoteItem.getId();
//        this.returnQuantity = returnNoteItem.getReturnQuantity();
//        this.returnPrice = returnNoteItem.getReturnPrice();
//        this.purchaseOrderItem = new PurchaseOrderItemVo(returnNoteItem.getPurchaseOrderItem(), null);
//        this.returnBundleQuantity = new BigDecimal(returnQuantity).divide(new BigDecimal(purchaseOrderItem.getSku().getCapacityInBundle()), 2, RoundingMode.HALF_UP);
//        this.returnTotal = returnPrice.multiply(new BigDecimal(returnQuantity));
//    }

    public ReturnNoteItemVo() {

    }
}
