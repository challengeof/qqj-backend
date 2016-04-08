package com.mishu.cgwy.accounting.domain;

import com.mishu.cgwy.profile.domain.Restaurant;
import lombok.Data;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Date;

/**
 * Created by admin on 2015/10/11.
 */
@Entity
@Data
public class RestaurantAccountHistory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(precision = 16, scale = 2)
    private BigDecimal amount;

    @Column(precision = 16, scale = 2)
    private BigDecimal unWriteoffAmount;

    private Date createDate;

    @Temporal(TemporalType.DATE)
    private Date accountDate;

    @ManyToOne
    @JoinColumn(name = "restaurant_id")
    private Restaurant restaurant;

    @ManyToOne
    @JoinColumn(name = "account_receivable_id")
    private AccountReceivable accountReceivable;

    @ManyToOne
    @JoinColumn(name = "collectionment_id")
    private Collectionment collectionment;

    @ManyToOne
    @JoinColumn(name = "account_receivable_writeoff_id")
    private AccountReceivableWriteoff accountReceivableWriteoff;
}
