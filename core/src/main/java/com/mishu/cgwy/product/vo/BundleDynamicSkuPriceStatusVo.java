package com.mishu.cgwy.product.vo;

import com.mishu.cgwy.inventory.domain.BundleDynamicSkuPriceStatus;
import lombok.Data;

import java.math.BigDecimal;

/**
 * Created by wangwei on 15/9/16.
 */
@Data
public class BundleDynamicSkuPriceStatusVo {

    private BigDecimal bundleSalePrice;

    private boolean bundleAvailable;

    private boolean bundleInSale;
}
