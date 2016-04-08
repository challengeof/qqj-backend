package com.mishu.cgwy.product.wrapper;

import com.mishu.cgwy.product.domain.SkuStatus;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Data
public class SkuPriceHistoryWrapper {

    private String city;

    private String warehouse;

    private String vendor;

    private Long productId;

    private Long skuId;

    private String name;

    private SkuStatus status;

    private String createDate;

    private String operator;

    private String reason;

    private Integer capacityInBundle;

    private String singleUnit;

    private String bundleUnit;

    private BigDecimal oldSingleSalePrice;

    private BigDecimal oldBundleSalePrice;

    private BigDecimal oldFixedPrice;

    private BigDecimal oldLastPurchasePrice;

    private BigDecimal oldAvgCost;

    private BigDecimal oldSingleSalePriceLimit;

    private BigDecimal oldBundleSalePriceLimit;

    private BigDecimal newSingleSalePrice;

    private BigDecimal newBundleSalePrice;

    private BigDecimal newFixedPrice;

    private BigDecimal newLastPurchasePrice;

    private BigDecimal newAvgCost;

    private BigDecimal newSingleSalePriceLimit;

    private BigDecimal newBundleSalePriceLimit;

    private BigDecimal price;
}
