package com.mishu.cgwy.stock.domain;

import com.mishu.cgwy.common.domain.City;
import com.mishu.cgwy.common.domain.Warehouse;
import com.mishu.cgwy.product.domain.Sku;
import lombok.Data;

import javax.persistence.*;
import java.math.BigDecimal;

/**
 * User: xudong
 * Date: 2/27/15
 * Time: 7:23 PM
 */
@Entity
@Data
@Table(uniqueConstraints = {@UniqueConstraint(columnNames={"city_id","sku_id"})})
public class StockTotal {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "city_id")
    private City city;

    @ManyToOne
    @JoinColumn(name = "sku_id")
    private Sku sku;

    private int quantity;

    @Column(precision = 16, scale = 6)
    private BigDecimal avgCost;

    @Column(precision = 16, scale = 6)
    private BigDecimal totalCost;

    @Version
    @Column(columnDefinition = "bigint default 0", nullable = false)
    private long version;

}
