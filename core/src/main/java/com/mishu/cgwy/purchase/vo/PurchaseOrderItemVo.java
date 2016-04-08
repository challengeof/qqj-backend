package com.mishu.cgwy.purchase.vo;

import com.mishu.cgwy.product.wrapper.SimpleSkuWrapper;
import com.mishu.cgwy.purchase.domain.PurchaseOrderItemStatus;
import lombok.Data;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Data
public class PurchaseOrderItemVo {

    private Long id;

    private Long skuId;

    private SimpleSkuWrapper sku;

    private String name;

    private BigDecimal rate;

    private Integer quantity;//库存

    private BigDecimal avgCost;

    private Integer purchaseQuantity;

    private Integer needQuantity;

    private BigDecimal needBundleQuantity;

    private Integer returnQuantity;

    private String singleUnit;

    private BigDecimal purchaseBundleQuantity;

    private String bundleUnit;

    private BigDecimal purchasePrice;

    private BigDecimal purchaseBundlePrice;

    private BigDecimal purchaseTotalPrice;

    private PurchaseOrderVo purchaseOrder;

    private Integer capacityInBundle;

    private BigDecimal fixedPrice;

    private BigDecimal lastPurchasePrice;

    private PurchaseOrderItemStatus status;

    //外采结果录入合单后，同一条记录对应的多个itemId
    private List<Long> purchaseOrderItemIds = new ArrayList<>();

    private Short sign;

//    public PurchaseOrderItemVo() {
//
//    }
//
//    public PurchaseOrderItemVo(PurchaseOrderItem purchaseOrderItem, StockTotal stockTotal) {
//        this.purchaseOrder = new PurchaseOrderWrapper(purchaseOrderItem.getPurchaseOrder());
//        this.id = purchaseOrderItem.getId();
//        this.sku = new SimpleSkuWrapper(purchaseOrderItem.getSku());
//        this.skuId = purchaseOrderItem.getSku().getId();
//        this.name = purchaseOrderItem.getSku().getName();
//        this.rate = purchaseOrderItem.getRate();
//        if (stockTotal == null) {
//            this.quantity = 0;
//            this.avgCost = new BigDecimal(0);
//        } else {
//            this.quantity = stockTotal.getQuantity();
//            this.avgCost = stockTotal.getAvgCost();
//        }
//        this.purchaseQuantity = purchaseOrderItem.getPurchaseQuantity() != null ? purchaseOrderItem.getPurchaseQuantity() : 0;
//        this.needQuantity = purchaseOrderItem.getNeedQuantity();
//        this.needBundleQuantity = new BigDecimal(needQuantity).divide(new BigDecimal(purchaseOrderItem.getSku().getCapacityInBundle()), 6, RoundingMode.HALF_UP);
//        this.returnQuantity = purchaseOrderItem.getReturnQuantity();
//        this.singleUnit = purchaseOrderItem.getSku().getSingleUnit();
//        this.purchaseBundleQuantity = new BigDecimal(purchaseQuantity).divide(new BigDecimal(purchaseOrderItem.getSku().getCapacityInBundle()), 6, RoundingMode.HALF_UP);
//        this.bundleUnit = purchaseOrderItem.getSku().getBundleUnit();
//        this.purchasePrice = purchaseOrderItem.getPrice();
//        this.purchaseBundlePrice = new BigDecimal(purchaseOrderItem.getSku().getCapacityInBundle()).multiply(this.purchasePrice).setScale(2, RoundingMode.HALF_UP);
//        this.purchaseTotalPrice = purchaseOrderItem.getPrice().multiply(new BigDecimal(purchaseQuantity)).setScale(6, RoundingMode.HALF_UP);
//        this.capacityInBundle = purchaseOrderItem.getSku().getCapacityInBundle();
//        this.status = PurchaseOrderItemStatus.get(purchaseOrderItem.getStatus());
//    }

    public void addPurchaseOrderItemId(Long purchaseOrderItemId) {
        purchaseOrderItemIds.add(purchaseOrderItemId);
    }
}
