package com.mishu.cgwy.stock.wrapper;

import com.mishu.cgwy.product.domain.Sku;
import com.mishu.cgwy.stock.domain.StockIn;
import com.mishu.cgwy.stock.domain.StockInItem;
import com.mishu.cgwy.stock.domain.StockInStatus;
import com.mishu.cgwy.stock.domain.StockInType;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * Created by xiao1zhao2 on 15/9/18.
 */
@Data
public class StockInItemWrapper {

    private Long stockInItemId;

    private Long stockInId;
    private Long sourceId;
    private Date receiveDate;
    private String depotName;
    private String stockInType;
    private String stockInStatus;
    private String vendorName;

    private Long skuId;
    private String skuName;
    private String skuSingleUnit;
    private String skuBundleUnit;
    private String specification;

    private int expectedQuantity;
    private int realQuantity;
    private int bundleQuantity;
    private Date productionDate;
    private BigDecimal price;
    private BigDecimal amount;
    private BigDecimal salePrice;
    private BigDecimal inAmount;

    public StockInItemWrapper(StockInItem stockInItem) {

        this.stockInItemId = stockInItem.getId();

        StockIn stockIn = stockInItem.getStockIn();
        this.stockInId = stockIn.getId();
        this.sourceId = stockIn.getPurchaseOrder() != null ? stockIn.getPurchaseOrder().getId() : stockIn.getSellReturn() != null ? stockIn.getSellReturn().getId() : stockIn.getTransfer() != null ? stockIn.getTransfer().getId() : null;
        this.receiveDate = stockIn.getReceiveDate();
        this.depotName = stockIn.getDepot().getName();
        this.stockInType = StockInType.fromInt(stockIn.getType()).getName();
        this.stockInStatus = StockInStatus.fromInt(stockIn.getStatus()).getName();
        this.vendorName = stockIn.getPurchaseOrder() != null ? stockIn.getPurchaseOrder().getVendor().getName() : "";

        Sku sku = stockInItem.getSku();
        this.skuId = sku.getId();
        this.skuName = sku.getName();
        this.skuSingleUnit = sku.getSingleUnit();
        this.skuBundleUnit = sku.getBundleUnit();
        this.specification = sku.getProduct().getSpecification();

        this.expectedQuantity = stockInItem.getExpectedQuantity();
        this.realQuantity = stockInItem.getRealQuantity();
        this.bundleQuantity = sku.getCapacityInBundle() != 0 ? stockInItem.getRealQuantity() / sku.getCapacityInBundle() : this.realQuantity;
        this.productionDate = stockInItem.getProductionDate();
        this.salePrice = stockInItem.getSalePrice() != null ? stockInItem.getSalePrice() : BigDecimal.ZERO;
        this.price = stockInItem.getPrice() != null ? stockInItem.getPrice() : BigDecimal.ZERO;
        this.amount = this.salePrice.multiply(new BigDecimal(this.realQuantity));
        this.inAmount= this.price.multiply(new BigDecimal(this.realQuantity));
    }
}
