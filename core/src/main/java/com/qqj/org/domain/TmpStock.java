package com.qqj.org.domain;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
@Getter
@Setter
public class TmpStock {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "tmp_customer_id")
    private TmpCustomer tmpCustomer;

    @Temporal(TemporalType.TIMESTAMP)
    private Date createTime;

    @OneToMany(cascade = {CascadeType.ALL}, orphanRemoval = true)
    @JoinColumn(name = "tmp_stock_id")
    private List<TmpStockItem> tmpStockItems = new ArrayList<TmpStockItem>();
}
