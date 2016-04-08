package com.mishu.cgwy.stock.wrapper;

import com.mishu.cgwy.stock.domain.StockTotalDaily;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Data
public class StockTotalDailyWrapper {

    private Long id;
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
    private Date createDate;

    public StockTotalDailyWrapper(StockTotalDaily stockTotalDaily) {
        this.id = stockTotalDaily.getId();
        this.cityName = stockTotalDaily.getCity().getName();
        this.skuId = stockTotalDaily.getSku().getId();
        this.skuName = stockTotalDaily.getSku().getName();
        this.quantity = stockTotalDaily.getQuantity();
        this.avgCost = stockTotalDaily.getAvgCost();
        this.totalCost = stockTotalDaily.getTotalCost();
        this.skuSingleUnit = stockTotalDaily.getSku().getSingleUnit();
        this.skuBundleUnit = stockTotalDaily.getSku().getBundleUnit();
        this.skuCapacityInBundle = stockTotalDaily.getSku().getCapacityInBundle();

        if (stockTotalDaily.getSku().getProduct().getCategory() != null) {
            if (stockTotalDaily.getSku().getProduct().getCategory().getParentCategory() != null) {
                if (stockTotalDaily.getSku().getProduct().getCategory().getParentCategory().getParentCategory() != null) {
                    this.categoryName = stockTotalDaily.getSku().getProduct().getCategory().getParentCategory().getParentCategory().getName();
                } else {
                    this.categoryName = stockTotalDaily.getSku().getProduct().getCategory().getParentCategory().getName();
                }
            } else {
                this.categoryName = stockTotalDaily.getSku().getProduct().getCategory().getName();
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
        this.createDate = stockTotalDaily.getCreateDate();
    }

}
