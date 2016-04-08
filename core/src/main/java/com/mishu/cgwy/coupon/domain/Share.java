package com.mishu.cgwy.coupon.domain;

import com.mishu.cgwy.coupon.constant.ShareTypeEnum;
import com.mishu.cgwy.profile.domain.Customer;
import lombok.Data;

import javax.persistence.*;
import java.util.Date;

/**
 * Created by wangguodong on 15/7/28.
 */
@Entity
@Data
public class Share {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn
    private Customer registrant;

    @ManyToOne
    @JoinColumn
    private Customer reference;

    @Temporal(TemporalType.TIMESTAMP)
    private Date createdTime;

    private Boolean couponSended = Boolean.FALSE;

    @JoinColumn
    private Integer shareType = ShareTypeEnum.coupon.val;
}
