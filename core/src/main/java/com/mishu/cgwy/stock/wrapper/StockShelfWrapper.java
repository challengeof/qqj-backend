package com.mishu.cgwy.stock.wrapper;

import com.mishu.cgwy.stock.domain.Stock;
import com.mishu.cgwy.stock.domain.StockStatus;
import lombok.Data;
import org.apache.commons.lang.time.DateUtils;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by Admin on 15-9-15.
 */
@Data
public class StockShelfWrapper {

    private Long id;
    private Long depotId;
    private String depotName;
    private Long skuId;
    private String skuName;
    private String skuSingleUnit;
    private String skuBundleUnit;
    private int skuCapacityInBundle;
    private BigDecimal taxRate;

    private int quantity = 0;
    private String bundleQuantityDes;
    private Date expirationDate;
    private Date productionDate;

    private String shelfCode;
    private String shelfName;

    private StockStatus stockStatus;
    private Integer shelfLife;
    private Date lastSaleDate;

    public StockShelfWrapper() {
    }

    public StockShelfWrapper(Stock stock) {

        this.id = stock.getId();
        this.depotId = stock.getDepot().getId();
        this.depotName = stock.getDepot().getName();
        this.skuId = stock.getSku().getId();
        this.skuName = stock.getSku().getName();
        this.skuSingleUnit = stock.getSku().getSingleUnit();
        this.skuBundleUnit = stock.getSku().getBundleUnit();
        this.skuCapacityInBundle = stock.getSku().getCapacityInBundle();
        this.taxRate = stock.getTaxRate();
        this.quantity = stock.getStock();
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

        this.expirationDate = stock.getExpirationDate();
        if (expirationDate != null && stock.getSku().getProduct().getShelfLife() != null) {
            this.productionDate = DateUtils.addDays(expirationDate, stock.getSku().getProduct().getShelfLife() * (-1));
            this.productionDate = DateUtils.truncate(productionDate, Calendar.DATE);
        }

        if (stock.getShelf() != null) {
            this.shelfCode = stock.getShelf().getShelfCode();
            this.shelfName = stock.getShelf().getName();
        }
        if (stock.getStockOut() == null && stock.getStockIn() == null && stock.getStockAdjust() == null) {
            this.stockStatus = StockStatus.AVAILABLE;
        } else if (stock.getStockOut() != null || stock.getStockAdjust() != null) {
            this.stockStatus = StockStatus.OCCUPIED;
        } else if (stock.getStockIn() != null) {
            this.stockStatus = StockStatus.ONROAD;
        } else {
            this.stockStatus = StockStatus.AVAILABLE;
        }
    }

}
