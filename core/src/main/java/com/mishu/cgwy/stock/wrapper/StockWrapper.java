package com.mishu.cgwy.stock.wrapper;

import com.mishu.cgwy.stock.domain.Stock;
import lombok.Data;
import org.apache.commons.lang.time.DateUtils;

import java.util.Calendar;
import java.util.Date;

/**
 * Created by Admin on 15-9-15.
 */
@Data
public class StockWrapper {

    private Long depotId;
    private String depotName;
    private Long skuId;
    private String skuName;
    private String skuSingleUnit;
    private String skuBundleUnit;
    private int skuCapacityInBundle;
    private String categoryName;

    private Long availableQuantity = 0L;
    private Long occupiedQuantity = 0L;
    private Long onRoadQuantity = 0L;

    private String bundleAvailableQuantityDes;
    private String bundleOccupiedQuantityDes;
    private String bundleOnRoadQuantityDes;

    private Date expirationDate;
    private Date productionDate;

    public StockWrapper() {
    }

    public StockWrapper(Stock stock) {
        this.depotId = stock.getDepot().getId();
        this.depotName = stock.getDepot().getName();
        this.skuId = stock.getSku().getId();
        this.skuName = stock.getSku().getName();
        this.skuSingleUnit = stock.getSku().getSingleUnit();
        this.skuBundleUnit = stock.getSku().getBundleUnit();
        this.skuCapacityInBundle = stock.getSku().getCapacityInBundle();
        if (stock.getSku().getProduct().getCategory() != null) {
            if (stock.getSku().getProduct().getCategory().getParentCategory() != null) {
                if (stock.getSku().getProduct().getCategory().getParentCategory().getParentCategory() != null) {
                    this.categoryName = stock.getSku().getProduct().getCategory().getParentCategory().getParentCategory().getName();
                } else {
                    this.categoryName = stock.getSku().getProduct().getCategory().getParentCategory().getName();
                }
            } else {
                this.categoryName = stock.getSku().getProduct().getCategory().getName();
            }
        }

        this.expirationDate = stock.getExpirationDate();
        if (expirationDate != null && stock.getSku().getProduct().getShelfLife() != null) {
            this.productionDate = DateUtils.addDays(expirationDate, stock.getSku().getProduct().getShelfLife() * (-1));
            this.productionDate = DateUtils.truncate(productionDate, Calendar.DATE);
        }
    }

    public void calBundleQuantity() {
        if (skuCapacityInBundle > 1) {
            int intNum = availableQuantity.intValue() / skuCapacityInBundle;
            int modNum = availableQuantity.intValue() % skuCapacityInBundle;
            if (intNum > 0) {
                if (modNum > 0) {
                    bundleAvailableQuantityDes = intNum + skuBundleUnit + modNum + skuSingleUnit;
                } else {
                    bundleAvailableQuantityDes = intNum + skuBundleUnit;
                }
            } else {
                bundleAvailableQuantityDes = availableQuantity.intValue() + skuSingleUnit;
            }

            intNum = occupiedQuantity.intValue() / skuCapacityInBundle;
            modNum = occupiedQuantity.intValue() % skuCapacityInBundle;
            if (intNum > 0) {
                if (modNum > 0) {
                    bundleOccupiedQuantityDes = intNum + skuBundleUnit + modNum + skuSingleUnit;
                } else {
                    bundleOccupiedQuantityDes = intNum + skuBundleUnit;
                }
            } else {
                bundleOccupiedQuantityDes = occupiedQuantity.intValue() + skuSingleUnit;
            }

            intNum = onRoadQuantity.intValue() / skuCapacityInBundle;
            modNum = onRoadQuantity.intValue() % skuCapacityInBundle;
            if (intNum > 0) {
                if (modNum > 0) {
                    bundleOnRoadQuantityDes = intNum + skuBundleUnit + modNum + skuSingleUnit;
                } else {
                    bundleOnRoadQuantityDes = intNum + skuBundleUnit;
                }
            } else {
                bundleOnRoadQuantityDes = onRoadQuantity.intValue() + skuSingleUnit;
            }
        } else {
            bundleAvailableQuantityDes = availableQuantity.intValue() + skuSingleUnit;
            bundleOccupiedQuantityDes = occupiedQuantity.intValue() + skuSingleUnit;
            bundleOnRoadQuantityDes = onRoadQuantity.intValue() + skuSingleUnit;
        }
    }

}
