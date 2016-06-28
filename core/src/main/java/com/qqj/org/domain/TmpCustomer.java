package com.qqj.org.domain;

import lombok.Data;

import javax.persistence.*;
import java.util.Date;

@Entity
@Data
public class TmpCustomer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private String certificateNumber;

    @ManyToOne
    private Customer parent;

    private Short level;

    private String username;

    private String password;

    private String telephone;

    private String address;

    @Temporal(TemporalType.TIMESTAMP)
    private Date createTime;

    private Short status;

    @ManyToOne
    private Team team;

    //直属总代
    @ManyToOne
    private Customer directLeader;

    @OneToOne(cascade = {CascadeType.ALL})
    @JoinColumn(name = "tmp_stock_id")
    private TmpStock tmpStock;
}
