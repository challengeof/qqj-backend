package com.mishu.cgwy.stock.wrapper;

import com.mishu.cgwy.stock.domain.StockTotal;
import lombok.Data;

import java.math.BigDecimal;

/**
 * Created by xiao1zhao2 on 15/9/15.
 */
@Data
public class StockTotalWrapper {

    private Long stockTotalId;
    private String cityName;
    private Long skuId;
    private String skuName;
    private int quantity;
    private BigDecimal avgCost;
    private BigDecimal totalCost;
    private String skuSingleUnit;
    private String skuBundleUnit;
    private int skuCapacityInBundle;
    private String bundleQuantityDes;
    private String categoryName;

    public StockTotalWrapper(StockTotal stockTotal) {
        this.stockTotalId = stockTotal.getId();
        this.cityName = stockTotal.getCity().getName();
        this.skuId = stockTotal.getSku().getId();
        this.skuName = stockTotal.getSku().getName();
        this.quantity = stockTotal.getQuantity();
        this.avgCost = stockTotal.getAvgCost();
        this.totalCost = stockTotal.getTotalCost();
        this.skuSingleUnit = stockTotal.getSku().getSingleUnit();
        this.skuBundleUnit = stockTotal.getSku().getBundleUnit();
        this.skuCapacityInBundle = stockTotal.getSku().getCapacityInBundle();

        if (stockTotal.getSku().getProduct().getCategory() != null) {
            if (stockTotal.getSku().getProduct().getCategory().getParentCategory() != null) {
                if (stockTotal.getSku().getProduct().getCategory().getParentCategory().getParentCategory() != null) {
                    this.categoryName = stockTotal.getSku().getProduct().getCategory().getParentCategory().getParentCategory().getName();
                } else {
                    this.categoryName = stockTotal.getSku().getProduct().getCategory().getParentCategory().getName();
                }
            } else {
                this.categoryName = stockTotal.getSku().getProduct().getCategory().getName();
            }
        }
        if (skuCapacityInBundle > 1) {
            int intNum = quantity / skuCapacityInBundle;
            int modNum = quantity % skuCapacityInBundle;
            if (intNum > 0) {
                if (modNum > 0) {
                    bundleQuantityDes = intNum + skuBundleUnit + modNum + skuSingleUnit;
                } else {
                    bundleQuantityDes = intNum + skuBundleUnit;
                }
            } else {
                bundleQuantityDes = quantity + skuSingleUnit;
            }
        } else {
            bundleQuantityDes = quantity + skuSingleUnit;
        }
    }

}
