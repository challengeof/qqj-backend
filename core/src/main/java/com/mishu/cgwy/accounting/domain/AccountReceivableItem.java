package com.mishu.cgwy.accounting.domain;

import com.mishu.cgwy.product.domain.Sku;
import lombok.Data;

import javax.persistence.*;
import java.math.BigDecimal;

/**
 * Created by admin on 2015/10/11.
 */
@Entity
@Data
public class AccountReceivableItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "sku_id")
    private Sku sku;

    private int quantity;

    @Column(precision = 16,scale = 6)
    private BigDecimal avgCost;

    @Column(precision = 10,scale = 2)
    private BigDecimal taxRate;

    @Column(precision = 16, scale = 6)
    private BigDecimal price;

    @ManyToOne
    @JoinColumn(name = "account_receivable_id")
    private AccountReceivable accountReceivable;

}
