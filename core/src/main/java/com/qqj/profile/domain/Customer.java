package com.qqj.profile.domain;

import com.qqj.admin.domain.AdminUser;
import lombok.Data;

import javax.persistence.*;
import java.util.Date;

/**
 * User: xudong
 * Date: 2/27/15
 * Time: 7:23 PM
 */
@Entity
@Data
public class Customer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 5000)
    private String code;

    private Integer level;

    private String username;

    private String password;

    private String telephone;

    private boolean enabled = true;

    @ManyToOne
    @JoinColumn(name = "admin_user_id")
    private AdminUser adminUser;

    @Temporal(TemporalType.TIMESTAMP)
    private Date createTime;
}
