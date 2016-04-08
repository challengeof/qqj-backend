package com.mishu.cgwy.product.wrapper;

import com.github.stuxuhai.jpinyin.PinyinFormat;
import com.github.stuxuhai.jpinyin.PinyinHelper;
import com.mishu.cgwy.product.domain.Product;
import com.mishu.cgwy.product.domain.Sku;
import com.mishu.cgwy.product.domain.SkuStatus;
import com.mishu.cgwy.product.domain.SkuTag;
import lombok.Data;
import org.apache.commons.lang.StringUtils;

import javax.persistence.Column;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Data
public class SkuPriceWrapper {

    private Long cityId;

    private String city;

    private String vendor;

    private Long productId;

    private Long skuId;

    private String name;

    private SkuStatus status;

    private Integer capacityInBundle;

    private String singleUnit;

    private String bundleUnit;

    private BigDecimal avgCost;

    private BigDecimal oldFixedPrice;

    private BigDecimal fixedPrice;

    private BigDecimal fixedPriceInc;

    private String fixedPriceIncRate;

    private BigDecimal oldLastPurchasePrice;

    private BigDecimal lastPurchasePrice;

    private BigDecimal lastPurchasePriceInc;

    private String lastPurchasePriceIncRate;

    private BigDecimal oldSingleSalePriceLimit;

    private BigDecimal singleSalePriceLimit;

    private BigDecimal singleSalePriceLimitInc;

    private String singleSalePriceLimitIncRate;

    private BigDecimal oldBundleSalePriceLimit;

    private BigDecimal bundleSalePriceLimit;

    private BigDecimal bundleSalePriceLimitInc;

    private String bundleSalePriceLimitIncRate;

    private BigDecimal oldSingleSalePrice;

    private BigDecimal singleSalePrice;

    private BigDecimal singleSalePriceInc;

    private String singleSalePriceIncRate;

    private BigDecimal oldBundleSalePrice;

    private BigDecimal bundleSalePrice;

    private BigDecimal bundleSalePriceInc;

    private String bundleSalePriceIncRate;

    private String modifyAdminUser;

    private String modifyTime;

    private String reason;
}
