package com.mishu.cgwy.product.domain;

import lombok.Data;

import javax.persistence.*;

import java.math.BigDecimal;
import java.util.Date;

@Entity
@Data
public class EdbSku {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Temporal(TemporalType.DATE)
    private Date stockDate;

    @ManyToOne
    @JoinColumn(name = "sku_id")
    private Sku sku;

    private BigDecimal avgPrice = BigDecimal.ZERO;

    private int stock = 0;
}
