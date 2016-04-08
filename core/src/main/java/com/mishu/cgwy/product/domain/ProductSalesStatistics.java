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
public class ProductSalesStatistics {
    @Id
    private Long id;

    @JoinColumn(name = "id")
    @OneToOne
    @MapsId
    private Product product;

//    private int saleCount;

    private int singleSaleCount;
    private int bundleSaleCount;
}
