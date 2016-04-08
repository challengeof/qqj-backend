package com.mishu.cgwy.bonus.domain;

import com.mishu.cgwy.admin.domain.AdminUser;
import com.mishu.cgwy.profile.domain.Restaurant;
import lombok.Data;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Date;

/**
 * User: xudong
 * Date: 5/29/15
 * Time: 10:57 AM
 */
@Entity
@Data
public class CustomerServiceBonus {
    public final static int RegisterBonus = 1;
    public final static int ConsumptionBonus = 2;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Date month;
    private int weekOfMonth;

    @ManyToOne
    @JoinColumn(name = "restaurant_id")
    private Restaurant restaurant;

    @ManyToOne
    @JoinColumn(name = "admin_user_id")
    private AdminUser adminUser;

    private BigDecimal bonus = BigDecimal.ZERO;
    private int bonusType;

}
