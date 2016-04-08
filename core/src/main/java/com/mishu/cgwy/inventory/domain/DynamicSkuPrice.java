package com.mishu.cgwy.inventory.domain;

import com.mishu.cgwy.common.domain.City;
import com.mishu.cgwy.common.domain.Warehouse;
import com.mishu.cgwy.product.domain.Sku;
import lombok.Data;

import javax.persistence.*;
import java.math.BigDecimal;

/**
 * User: xudong
 * Date: 2/27/15
 * Time: 10:06 PM
 */
@Entity
@Data
public class DynamicSkuPrice {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "sku_id")
    private Sku sku;

    @ManyToOne
    @JoinColumn(name = "warehouse_id")
    private Warehouse warehouse;

    @Embedded
    private SingleDynamicSkuPriceStatus singlePriceStatus;

    @Embedded
    private BundleDynamicSkuPriceStatus bundlePriceStatus;

    @Override
    public String toString() {
        return "DynamicSkuPrice{" +
                "id=" + id +
                ", singlePriceStatus=" + singlePriceStatus +
                ", bundlePriceStatus=" + bundlePriceStatus +
                '}';
    }
}
