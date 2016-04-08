package com.mishu.cgwy.stock.domain;

import com.mishu.cgwy.common.domain.City;
import com.mishu.cgwy.product.domain.Sku;
import lombok.Data;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Date;

@Entity
@Data
public class StockTotalDaily {

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

    private Date createDate;

}
