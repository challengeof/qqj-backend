package com.mishu.cgwy.coupon.domain;

import com.mishu.cgwy.admin.domain.AdminUser;
import com.mishu.cgwy.order.domain.Order;
import com.mishu.cgwy.profile.domain.Customer;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;
import java.util.Date;
import java.util.Set;

/**
 * Created by bowen on 15-6-24.
 */
@Entity
@Data
@EqualsAndHashCode(exclude={"orders"})
public class CustomerCoupon {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private int status = CouponStatus.UNUSED.getValue();

    @ManyToMany(fetch = FetchType.LAZY,mappedBy="customerCoupons")
    private Set<Order> orders;

    @ManyToOne
    @JoinColumn(name = "sender")
    private AdminUser sender;

    @ManyToOne
    @JoinColumn(name = "customer_id")
    private Customer customer;

    @ManyToOne
    @JoinColumn(name = "coupon_id")
    private Coupon coupon;

    @Temporal(TemporalType.TIMESTAMP)
    private Date start;

    @Temporal(TemporalType.TIMESTAMP)
    private Date end;
    
    @Temporal(TemporalType.TIMESTAMP)
    private Date sendDate;

    @Temporal(TemporalType.TIMESTAMP)
    private Date useDate;

    private Short reason;

    private String remark;

    @ManyToOne
    @JoinColumn(name = "operater")
    private AdminUser operater;  //状态 操作人

    private Date operateTime;  // 操作时间

}
