//package com.mishu.cgwy.coupon.domain;
//
//import com.mishu.cgwy.order.domain.Order;
//import lombok.Getter;
//import lombok.Setter;
//
//import javax.persistence.*;
//
//@Entity
//@Getter
//@Setter
//public class CouponRule {
//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    private Long id;
//
//    @ManyToOne
//    @JoinColumn(name = "coupon_id")
//    private Order order;
//
//    private String ruleKey;
//
//    private String ruleValue;
//}
