package com.mishu.cgwy.stock.wrapper;

import com.mishu.cgwy.product.domain.Sku;
import com.mishu.cgwy.stock.domain.StockOut;
import com.mishu.cgwy.stock.domain.StockOutItem;
import com.mishu.cgwy.stock.domain.StockOutItemStatus;
import com.mishu.cgwy.stock.domain.StockOutStatus;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * Created by xiao1zhao2 on 15/9/18.
 */
@Data
public class StockOutItemWrapper {

    private Long stockOutItemId;

    private Long stockOutId;
    private Date sendDate;
    private Date receiveDate;
    private String depotName;
    private String stockOutStatus;

    private Long orderId;
    private String customerName;
    private String trackerName;

    private Long purchaseId;
    private String vendorName;
    private BigDecimal purchaseAmount;

    private Long transferId;
    private String sourceDepotName;
    private String targetDepotName;
    private Date transferDate;
    private Date auditDate;

    private boolean bundle;

    private Long skuId;
    private String skuName;
    private String specification;
    private String skuSingleUnit;
    private String skuBundleUnit;

    private int expectedQuantity;
    private int realQuantity;
    private int receiveQuantity;
    private int bundleQuantity;
    private BigDecimal price;
    private BigDecimal amount;
    private String stockOutItemStatus;
    private int stockOutItemStatusValue;
    private BigDecimal avgCost;
    private BigDecimal taxRate;
    private int capacityInBundle;
    private Long categoryId;

    public StockOutItemWrapper() {
    }

    public StockOutItemWrapper(StockOutItem stockOutItem) {

        this.stockOutItemId = stockOutItem.getId();

        StockOut stockOut = stockOutItem.getStockOut();
        this.stockOutId = stockOut.getId();
        this.sendDate = stockOut.getFinishDate();
        this.receiveDate = stockOut.getReceiveDate();
        this.depotName = stockOut.getDepot().getName();
        this.stockOutStatus = StockOutStatus.fromInt(stockOut.getStatus()).getName();

        if (stockOut.getOrder() != null) {
            this.orderId = stockOut.getOrder().getId();
            this.customerName = stockOut.getOrder().getRestaurant().getName();
            this.trackerName = stockOut.getOrderGroup() != null && stockOut.getOrderGroup().getTracker() != null ? stockOut.getOrderGroup().getTracker().getRealname() : null;
        }
        if (stockOut.getReturnNote() != null) {
            this.purchaseId = stockOut.getReturnNote().getPurchaseOrder().getId();
            this.vendorName = stockOut.getReturnNote().getPurchaseOrder().getVendor().getName();
            this.purchaseAmount = stockOutItem.getPurchasePrice().multiply(new BigDecimal(stockOutItem.getRealQuantity()));
        }
        if (stockOut.getTransfer() != null) {
            this.transferId = stockOut.getTransfer().getId();
            this.sourceDepotName = stockOut.getDepot().getName();
            this.targetDepotName = stockOut.getTransfer().getTargetDepot().getName();
            this.transferDate = stockOut.getTransfer().getCreateDate();
            this.auditDate = stockOut.getTransfer().getAuditDate();
        }

        Sku sku = stockOutItem.getSku();
        this.skuId = sku.getId();
        this.skuName = sku.getName();
        this.specification = sku.getProduct().getSpecification();
        this.skuSingleUnit = sku.getSingleUnit();
        this.skuBundleUnit = sku.getBundleUnit();

        this.expectedQuantity = stockOutItem.getExpectedQuantity();
        this.realQuantity = stockOutItem.getRealQuantity();
        this.receiveQuantity = stockOutItem.getReceiveQuantity();
        this.bundleQuantity = sku.getCapacityInBundle() != 0 ? this.receiveQuantity / sku.getCapacityInBundle() : this.receiveQuantity;
        this.price = stockOutItem.getPrice() != null ? stockOutItem.getPrice() : BigDecimal.ZERO;
        this.amount = this.price.multiply(new BigDecimal(stockOutItem.getReceiveQuantity()));
        this.stockOutItemStatus = StockOutItemStatus.fromInt(stockOutItem.getStatus()).getName();
        this.stockOutItemStatusValue = stockOutItem.getStatus();
        this.avgCost = stockOutItem.getAvgCost();
        this.taxRate = stockOutItem.getTaxRate();
        this.bundle = stockOutItem.isBundle();
        this.capacityInBundle = sku.getCapacityInBundle();
    }
}
