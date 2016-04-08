package com.mishu.cgwy.bonus.domain;

import com.mishu.cgwy.admin.domain.AdminUser;
import lombok.Data;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Date;

/**
 * User: xudong
 * Date: 5/29/15
 * Time: 3:15 PM
 */
@Data
@Entity
public class CustomerServiceStatistics {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Temporal(TemporalType.DATE)
    private Date month;

    @ManyToOne
    @JoinColumn(name = "admin_user_id")
    private AdminUser adminUser;

    private BigDecimal bonus = BigDecimal.ZERO;
    private long newRestaurantCount = 0;
    private long restaurantHavingOrderCount = 0;
    private long complaintCount = 0;
    private BigDecimal consumption = BigDecimal.ZERO;



}
