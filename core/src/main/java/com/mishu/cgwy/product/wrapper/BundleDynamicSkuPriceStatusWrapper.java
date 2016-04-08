package com.mishu.cgwy.product.wrapper;

import com.mishu.cgwy.inventory.domain.BundleDynamicSkuPriceStatus;
import lombok.Data;

import java.math.BigDecimal;

/**
 * Created by wangwei on 15/9/16.
 */
@Data
@Deprecated
public class BundleDynamicSkuPriceStatusWrapper {

    private BigDecimal bundleSalePrice;

    private boolean bundleAvailable;

    private boolean bundleInSale;

    public BundleDynamicSkuPriceStatusWrapper() {

    }

    public BundleDynamicSkuPriceStatusWrapper(BundleDynamicSkuPriceStatus bundleDynamicSkuPriceStatus) {
        this.bundleSalePrice = bundleDynamicSkuPriceStatus.getBundleSalePrice();
        this.bundleAvailable = bundleDynamicSkuPriceStatus.isBundleAvailable();
        this.bundleInSale = bundleDynamicSkuPriceStatus.isBundleInSale();
    }
}
