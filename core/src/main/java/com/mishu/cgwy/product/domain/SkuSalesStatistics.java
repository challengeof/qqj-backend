package com.mishu.cgwy.product.domain;

import lombok.Data;

import javax.persistence.*;

/**
 * User: xudong
 * Date: 3/25/15
 * Time: 11:57 AM
 */
@Entity
@Data
public class SkuSalesStatistics {
    @Id
    private Long id;

    @JoinColumn(name = "ID")
    @OneToOne
    @MapsId
    private Sku sku;

    private long salesCount;
}
