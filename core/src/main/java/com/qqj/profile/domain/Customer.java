package com.qqj.profile.domain;

import lombok.Data;

import javax.persistence.*;
import java.util.Date;

@Entity
@Data
public class Customer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 5000)
    private String code;

    @Column(length = 5000)
    private String parent;

    private Integer level;

    private String username;

    private String password;

    private String telephone;

    private String address;

    private boolean enabled = true;

    @Temporal(TemporalType.TIMESTAMP)
    private Date createTime;

    private
}
