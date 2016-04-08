//package com.mishu.cgwy.profile.domain;
//
//import com.mishu.cgwy.admin.domain.AdminUser;
//import lombok.Data;
//
//import javax.persistence.*;
//import java.util.Date;
//
///**
// * 餐馆销售 分配认领日志
// * Created by king-ck on 2016/2/29.
// */
//@Entity
////@Table(indexes = {@Index(name = "RESTAURANT_SELL_ALLOT_INDEX", columnList = "telephone", unique = false)})
//@Data
//public class RestaurantSellerLog {
//
//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    private Long id;
//
//    @ManyToOne
//    @JoinColumn(name = "customer_id")
//    private Customer customer;
//    @ManyToOne
//    private AdminUser seller;
//
//    private Date followBegin;
//
//    private Date followEnd;
//
//    private Integer sellerType; //开发   维护
//    private Integer operateStatus; //  通过   驳回
//
//    @ManyToOne
//    private AdminUser operater; // 审核人
//    private Date operateTime; // 审核操作时间
//
//    @ManyToOne
//    private AdminUser createUser; //发起人
//
//    private Integer type; //此条记录的类型  1. 分配 2.认领
//
//    private Date createDate;
//
//
//}
