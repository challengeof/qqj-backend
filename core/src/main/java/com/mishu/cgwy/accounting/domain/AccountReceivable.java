package com.mishu.cgwy.accounting.domain;

import com.mishu.cgwy.admin.domain.AdminUser;
import com.mishu.cgwy.profile.domain.Restaurant;
import com.mishu.cgwy.stock.domain.StockIn;
import com.mishu.cgwy.stock.domain.StockOut;
import lombok.Data;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by admin on 2015/10/11.
 */
@Entity
@Data
public class AccountReceivable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(precision = 16, scale = 2)
    private BigDecimal amount;

    @Column(precision = 16, scale = 2)
    private BigDecimal writeOffAmount;

    private Date createDate;

    @Temporal(TemporalType.DATE)
    private Date writeOffDate;

    @ManyToOne
    @JoinColumn(name = "writeOffer_id")
    private AdminUser writeOffer;

    private int status;

    private int type;

    @ManyToOne
    @JoinColumn(name = "restaurant_id")
    private Restaurant restaurant;

    @ManyToOne
    @JoinColumn(name = "stock_out_id")
    private StockOut stockOut;

    @ManyToOne
    @JoinColumn(name = "stock_in_id")
    private StockIn stockIn;

    @OneToMany(cascade = {CascadeType.ALL}, orphanRemoval = true)
    @JoinColumn(name = "account_receivable_id")
    private List<AccountReceivableItem> accountReceivableItems = new ArrayList<>();

}
