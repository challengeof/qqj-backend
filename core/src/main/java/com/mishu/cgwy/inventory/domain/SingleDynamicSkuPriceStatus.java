package com.mishu.cgwy.inventory.domain;

import com.mishu.cgwy.product.wrapper.SingleDynamicSkuPriceStatusWrapper;
import lombok.Data;

import javax.persistence.Embeddable;
import java.math.BigDecimal;

/**
 * Created by wangwei on 15/9/15.
 */
@Embeddable
@Data
public class SingleDynamicSkuPriceStatus {

    private BigDecimal singleSalePrice = BigDecimal.ZERO;

    private boolean singleAvailable = false;

    private boolean singleInSale = false;

    public SingleDynamicSkuPriceStatus() {}

    public SingleDynamicSkuPriceStatus(SingleDynamicSkuPriceStatusWrapper singleDynamicSkuPriceStatus) {
        this.singleSalePrice = singleDynamicSkuPriceStatus.getSingleSalePrice();
        this.singleAvailable = singleDynamicSkuPriceStatus.isSingleAvailable();
        this.singleInSale = singleDynamicSkuPriceStatus.isSingleInSale();
    }

    @Override
    public String toString() {
        return "SingleDynamicSkuPriceStatus{" +
                "singleSalePrice=" + singleSalePrice +
                ", singleAvailable=" + singleAvailable +
                ", singleInSale=" + singleInSale +
                '}';
    }
}
