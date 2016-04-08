package com.mishu.cgwy.stock.domain;

import com.mishu.cgwy.admin.domain.AdminUser;
import com.mishu.cgwy.order.domain.Order;
import lombok.Data;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * User: xudong
 * Date: 9/15/15
 * Time: 3:14 PM
 */
@Entity
@Data
public class SellReturn {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private int type;

    private int status;

    @Temporal(TemporalType.TIMESTAMP)
    private Date createDate;

    @ManyToOne
    @JoinColumn(name = "creator_id")
    private AdminUser creator;

    @Temporal(TemporalType.TIMESTAMP)
    private Date auditDate;

    @ManyToOne
    @JoinColumn(name = "auditor_id")
    private AdminUser auditor;

    private String auditOpinion;

    @Column(precision = 16, scale = 2)
    private BigDecimal amount;

    @ManyToOne
    @JoinColumn(name = "depot_id")
    private Depot depot;

    @OneToMany(cascade = {CascadeType.ALL}, orphanRemoval = true)
    @JoinColumn(name = "sell_return_id")
    private List<SellReturnItem> sellReturnItems = new ArrayList<>();

    @ManyToOne
    @JoinColumn(name = "order_id")
    private Order order;
}
