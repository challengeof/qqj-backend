package com.mishu.cgwy.stock.domain;

import com.mishu.cgwy.admin.domain.AdminUser;
import com.mishu.cgwy.order.domain.Order;
import com.mishu.cgwy.profile.domain.Customer;
import lombok.Data;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by wangwei on 15/10/13.
 */
@Entity
@Data
public class SellCancel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private int type;

    @Temporal(TemporalType.TIMESTAMP)
    private Date createDate;

    @ManyToOne
    @JoinColumn(name = "creator_id")
    private AdminUser creator;

    @ManyToOne
    @JoinColumn(name = "customer_id")
    private Customer customer;

    @Column(precision = 16, scale = 2)
    private BigDecimal amount;

    @OneToMany(cascade = {CascadeType.ALL}, orphanRemoval = true)
    @JoinColumn(name = "sell_cancel_id")
    private List<SellCancelItem> sellCancelItems = new ArrayList<>();

    @ManyToOne
    @JoinColumn(name = "order_id")
    private Order order;

    @Override
    public String toString() {
        return "SellCancel{" +
                "id=" + id +
                ", type=" + type +
                ", createDate=" + createDate +
                ", amount=" + amount +
                '}';
    }
}
