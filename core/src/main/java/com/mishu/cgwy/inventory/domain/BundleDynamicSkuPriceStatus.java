package com.mishu.cgwy.inventory.domain;

import com.mishu.cgwy.product.wrapper.BundleDynamicSkuPriceStatusWrapper;
import lombok.Data;

import javax.persistence.Embeddable;
import java.math.BigDecimal;

/**
 * Created by wangwei on 15/9/15.
 */
@Embeddable
@Data
public class BundleDynamicSkuPriceStatus {

    private BigDecimal bundleSalePrice = BigDecimal.ZERO;

    private boolean bundleAvailable = false;

    private boolean bundleInSale = false;

    public BundleDynamicSkuPriceStatus() {}

    public BundleDynamicSkuPriceStatus(BundleDynamicSkuPriceStatusWrapper bundleDynamicSkuPriceStatus) {
        this.bundleSalePrice = bundleDynamicSkuPriceStatus.getBundleSalePrice();
        this.bundleAvailable = bundleDynamicSkuPriceStatus.isBundleAvailable();
        this.bundleInSale = bundleDynamicSkuPriceStatus.isBundleInSale();
    }

    @Override
    public String toString() {
        return "BundleDynamicSkuPriceStatus{" +
                "bundleSalePrice=" + bundleSalePrice +
                ", bundleAvailable=" + bundleAvailable +
                ", bundleInSale=" + bundleInSale +
                '}';
    }
}
