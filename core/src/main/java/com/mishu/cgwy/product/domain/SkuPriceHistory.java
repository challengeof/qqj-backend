package com.mishu.cgwy.product.domain;

import com.mishu.cgwy.admin.domain.AdminUser;
import com.mishu.cgwy.common.domain.City;
import com.mishu.cgwy.common.domain.Warehouse;
import com.mishu.cgwy.inventory.domain.Vendor;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Date;

@Entity
@Getter
@Setter
public class SkuPriceHistory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "sku_id")
    private Sku sku;

    @ManyToOne
    @JoinColumn(name = "city_id")
    private City city;

    @ManyToOne
    @JoinColumn(name = "vendor_id")
    private Vendor vendor;

    private Date createDate;

    @ManyToOne
    @JoinColumn(name = "operator")
    private AdminUser operator;

    @Column(precision = 16, scale = 6)
    private BigDecimal singleSalePriceLimit;

    @Column(precision = 16, scale = 6)
    private BigDecimal bundleSalePriceLimit;

    //销售定价
    @Column(precision = 16, scale = 6)
    private BigDecimal fixedPrice;

    private BigDecimal singleSalePrice;

    private BigDecimal bundleSalePrice;

    private String reason;

    private Integer type;

    @Column(precision = 16, scale = 6)
    private BigDecimal purchasePrice;
}
