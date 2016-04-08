package com.mishu.cgwy.product.domain;

import com.mishu.cgwy.common.domain.City;
import lombok.Data;

import javax.persistence.*;

/**
 * Created by wangwei on 15/12/1.
 */
@Entity
@Data
public class SkuTag {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "sku_id")
    private Sku sku;

    @ManyToOne
    @JoinColumn(name = "city_id")
    private City city;

    private Boolean inDiscount = Boolean.TRUE;

    private Integer limitedQuantity;

    @Override
    public String toString() {
        return "SkuTag{" +
                "id=" + id +
                ", InDiscount=" + inDiscount +
                '}';
    }
}
