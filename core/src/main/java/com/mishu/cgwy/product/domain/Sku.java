package com.mishu.cgwy.product.domain;

import com.mishu.cgwy.inventory.domain.DynamicSkuPrice;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

/**
 * User: xudong
 * Date: 2/27/15
 * Time: 7:33 PM
 */
@Entity
@Data
@EqualsAndHashCode(exclude={"dynamicSkuPrice"})
public class Sku {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "product_id")
    private Product product;

    private BigDecimal marketPrice = BigDecimal.ZERO;

    private BigDecimal rate;

    private int capacityInBundle = 1;

    private int status;

    private String singleUnit;
    @Column(precision = 10,scale = 2)
    private BigDecimal singleGross_wight;  //毛重
    @Deprecated
    private BigDecimal singleNet_weight; //净重
    private BigDecimal singleLong;
    private BigDecimal singleWidth;
    private BigDecimal singleHeight;

    private String bundleUnit;
    @Column(precision = 10,scale = 2)
    private BigDecimal bundleGross_wight;  //毛重
    @Deprecated
    private BigDecimal bundleNet_weight; //净重
    private BigDecimal bundleLong;
    private BigDecimal bundleWidth;
    private BigDecimal bundleHeight;

    private Date createDate = new Date();

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "sku")
    private List<DynamicSkuPrice> dynamicSkuPrice;

    @OneToMany(cascade = {CascadeType.ALL}, orphanRemoval = true)
    @JoinColumn(name = "sku_id")
    private List<SkuTag> skuTags = new ArrayList<>();

    @Transient
    public String getName() {
        return product.getName();
    }

    @Transient
    public String getProductNameFormat() {
        final String name = product.getName();
        final String[] split = name.split("/");
        if (split.length > 1) {
            return split[1];
        } else {
            return "";
        }
    }

    @Override
    public String toString() {
        return "Sku{" +
                "id=" + id +
                ", product=" + product +
                ", marketPrice=" + marketPrice +
                '}';
    }

    @Override
    public boolean equals(Object obj) {
        return id.equals(((Sku)obj).getId());
    }

    @Override
    public int hashCode() {
        return id.intValue();
    }
}
