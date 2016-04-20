package com.qqj.org.domain;

import lombok.Data;

import javax.persistence.*;
import java.util.Date;

@Entity
@Data
public class Customer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 5000, unique = true)
    private String code;

    private String name;

    private String certificateNumber;

    @ManyToOne
    private Customer parent;

    private Short level;

    private boolean isFounder = false;

    private String username;

    private String password;

    private String telephone;

    private String address;

    @Temporal(TemporalType.TIMESTAMP)
    private Date createTime;

    @ManyToOne
    private Customer creator;

    private Short status;

    @ManyToOne
    private Team team;
}
