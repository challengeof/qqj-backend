package com.mishu.cgwy.product.dto;

import com.mishu.cgwy.product.constants.Constants;
import com.mishu.cgwy.product.domain.ProductSalesStatistics;
import com.mishu.cgwy.product.vo.SkuVo;
import com.mishu.cgwy.product.wrapper.BundleDynamicSkuPriceStatusWrapper;
import com.mishu.cgwy.product.wrapper.SingleDynamicSkuPriceStatusWrapper;
import com.mishu.cgwy.product.wrapper.SkuWrapper;
import lombok.Data;

import java.math.BigDecimal;

/**
 * Created by kaicheng on 3/22/15.
 */


/**
 * web
 */
@Data
public class ProductItem {
    private String productNumber;
    private String name;
//    private BigDecimal price;
    private BigDecimal marketPrice;
//    private Integer sellCount;
    private Integer singleSaleCount;
    private Integer bundleSaleCount;
    private BundleDynamicSkuPriceStatusWrapper bundlePrice;
    private SingleDynamicSkuPriceStatusWrapper singlePrice;
    private Long brandId;
    private String url;
//    private Integer maxBuy;
    private Integer singleMaxBuy;
    private Integer bundleMaxBuy;

    public ProductItem(SkuWrapper sku) {
        setName(sku.getName());
//        setUrl(sku.getMediaFileUrl());
        if (sku.getBrand() != null) {
            setBrandId(sku.getBrand().getId());
        }

        setProductNumber(String.valueOf(sku.getId()));

        // TODO 引入销量
        setSingleSaleCount(Constants.SELL_COUNT);
        setBundleSaleCount(Constants.SELL_COUNT);
//        setPrice(sku.getSalePrice());
        setSinglePrice(sku.getSinglePrice());
        setBundlePrice(sku.getBundlePrice());
        setMarketPrice(sku.getMarketPrice());
//        setMaxBuy(Constants.MAX_BUY);
        setSingleMaxBuy(Constants.MAX_BUY);
        setBundleMaxBuy(Constants.MAX_BUY);
    }

    public String getMarketPrice() {
        if (marketPrice != null) {
            return marketPrice.toString();
        } else {
            return "0";
        }

    }

    public ProductItem(SkuWrapper sku, ProductSalesStatistics productSalesStatistics) {
        this(sku);
        if (productSalesStatistics != null) {
            setSingleSaleCount(productSalesStatistics.getSingleSaleCount());
            setBundleSaleCount(productSalesStatistics.getBundleSaleCount());
        }
    }
}