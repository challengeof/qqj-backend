package com.qqj.org.domain;

import lombok.Data;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
@Data
public class Customer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long leftCode;

    private Long rightCode;

    private String name;

    private String certificateNumber;

    @ManyToOne
    private Customer parent;

    private Short level;

    private boolean founder = false;

    private String username;

    private String password;

    private String telephone;

    private String address;

    @Temporal(TemporalType.TIMESTAMP)
    private Date createTime;

    private Short status;

    @ManyToOne
    private Team team;

    @OneToMany(cascade = {CascadeType.ALL}, orphanRemoval = true)
    @JoinColumn(name = "customer_id")
    private List<Stock> stocks = new ArrayList<Stock>();
}
