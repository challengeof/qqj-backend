package com.mishu.cgwy.coupon.domain;

import com.mishu.cgwy.product.domain.Sku;
import lombok.Data;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Date;

/**
 * Created by bowen on 15-6-23.
 */
@Entity
@Data
public class Coupon {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @Temporal(TemporalType.TIMESTAMP)
    private Date start;

    @Temporal(TemporalType.TIMESTAMP)
    private Date end;

    private String description;

    private String remark;

    // MVEL : https://github.com/mvel/mvel
    private String sendRule;
    private String useRule;

    private BigDecimal discount = BigDecimal.ZERO;

    private Integer couponConstants;

    @JoinColumn(name = "sku_id")
    @OneToOne
    private Sku sku;

    private Integer quantity = 0;

    @Temporal(TemporalType.TIMESTAMP)
    private Date createTime;

    @Temporal(TemporalType.TIMESTAMP)
    private Date deadline;

    private Integer periodOfValidity;

    private Long score;

    private int sendCouponQuantity = 1;

    private int beginningDays;

    @Column(length = 2000)
    private String ruleValue;

    private Integer couponRestriction;
}
