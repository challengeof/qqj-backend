package com.mishu.cgwy.product.domain;

import com.mishu.cgwy.common.domain.City;
import com.mishu.cgwy.common.domain.Warehouse;
import com.mishu.cgwy.inventory.domain.Vendor;
import lombok.Data;

import javax.persistence.*;
import java.math.BigDecimal;

@Entity
@Data
public class SkuPrice {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "city_id")
    private City city;

    @ManyToOne
    @JoinColumn(name = "sku_id")
    private Sku sku;

    @Column(precision = 16, scale = 6)
    private BigDecimal fixedPrice;

    @Column(precision = 16, scale = 6)
    private BigDecimal purchasePrice;

    @Column(precision = 16, scale = 6)
    private BigDecimal singleSalePriceLimit;

    @Column(precision = 16, scale = 6)
    private BigDecimal bundleSalePriceLimit;

    private BigDecimal singleSalePrice;

    private BigDecimal bundleSalePrice;

    @Column(precision = 16, scale = 6)
    private BigDecimal oldFixedPrice;

    @Column(precision = 16, scale = 6)
    private BigDecimal oldPurchasePrice;

    @Column(precision = 16, scale = 6)
    private BigDecimal oldSingleSalePriceLimit;

    @Column(precision = 16, scale = 6)
    private BigDecimal oldBundleSalePriceLimit;

    private BigDecimal oldSingleSalePrice;

    private BigDecimal oldBundleSalePrice;
}
