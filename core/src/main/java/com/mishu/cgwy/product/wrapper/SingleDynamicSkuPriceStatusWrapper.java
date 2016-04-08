package com.mishu.cgwy.product.wrapper;

import com.mishu.cgwy.inventory.domain.SingleDynamicSkuPriceStatus;
import lombok.Data;

import java.math.BigDecimal;

/**
 * Created by wangwei on 15/9/16.
 */
@Data
@Deprecated
public class SingleDynamicSkuPriceStatusWrapper {

    private BigDecimal singleSalePrice;

    private boolean singleAvailable;

    private boolean singleInSale;

    public SingleDynamicSkuPriceStatusWrapper() {

    }

    public SingleDynamicSkuPriceStatusWrapper(SingleDynamicSkuPriceStatus singleDynamicSkuPriceStatus) {
        this.singleSalePrice = singleDynamicSkuPriceStatus.getSingleSalePrice();
        this.singleAvailable = singleDynamicSkuPriceStatus.isSingleAvailable();
        this.singleInSale = singleDynamicSkuPriceStatus.isSingleInSale();
    }
}
