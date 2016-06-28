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
public class Stock {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "customer_id")
    private Customer customer;

    @Temporal(TemporalType.TIMESTAMP)
    private Date createTime;

    @OneToMany(cascade = {CascadeType.ALL}, orphanRemoval = true)
    @JoinColumn(name = "stock_id")
    private List<StockItem> stockItems = new ArrayList<StockItem>();
}
