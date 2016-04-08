package com.mishu.cgwy.stock.domain;

import com.mishu.cgwy.product.domain.Sku;
import lombok.Data;

import javax.persistence.*;
import java.math.BigDecimal;

/**
 * Created by admin on 2015/9/17.
 */
@Entity
@Data
public class SellReturnItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private int quantity;

    @ManyToOne
    @JoinColumn(name = "sku_id")
    private Sku sku;

    @Column(precision = 16,scale = 6)
    private BigDecimal avgCost;

    @Column(precision = 10,scale = 2)
    private BigDecimal taxRate;

    @Column(precision = 16, scale = 6)
    private BigDecimal price;

    private boolean bundle;

    @ManyToOne
    @JoinColumn(name = "sell_return_reason_id")
    private SellReturnReason sellReturnReason;

    @ManyToOne
    @JoinColumn(name = "sell_return_id")
    private SellReturn sellReturn;

    private String memo = "";
}
