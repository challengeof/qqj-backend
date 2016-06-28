package com.qqj.purchase.domain;

import com.qqj.org.domain.Customer;
import com.qqj.org.domain.Team;
import lombok.Data;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
@Data
public class Purchase {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private Team team;

    @ManyToOne
    private Customer customer;

    //直属总代
    @ManyToOne
    private Customer directLeader;

    @Temporal(TemporalType.TIMESTAMP)
    private Date createTime;

    private Short status;

    @OneToMany(cascade = {CascadeType.ALL}, orphanRemoval = true)
    @JoinColumn(name = "purchase_id")
    private List<PurchaseItem> purchaseItems = new ArrayList<PurchaseItem>();
}
