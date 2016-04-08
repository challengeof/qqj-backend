package com.mishu.cgwy.profile.domain;

import com.mishu.cgwy.admin.domain.AdminUser;
import com.mishu.cgwy.order.domain.Order;
import lombok.Data;
import lombok.ToString;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * User: xudong
 * Date: 2/27/15
 * Time: 7:23 PM
 */
@Entity
@Table(indexes = {@Index(name = "RESTAURANT_TELEPHONE_INDEX", columnList = "telephone", unique = false)})
@Data
public class Restaurant {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private Address address;
    private String license;
    private int status;

    private String receiver; //联系人
    private String telephone; //联系人电话


    @ManyToOne
    @JoinColumn(name = "type_id")
    private RestaurantType type;//餐馆类型

    @ManyToOne
    @JoinColumn(name = "customer_id")
    private Customer customer;



    private Short grade;

    @Temporal(TemporalType.TIMESTAMP)
    private Date lastPurchaseTime;

    private Boolean warning;

    private Boolean openWarning = Boolean.TRUE;

    /**
     * 首次审核通过时间。
     * 审核通过时，如果此字段为空，填写此字段。
     * 发送注册优惠券时，如果首次审核通过时间处于注册活动时间内，则发送优惠券，否则不发送。
     */
    @Temporal(TemporalType.TIMESTAMP)
    private Date auditTime;

    private Integer restaurantReason;

    @OneToMany(mappedBy = "restaurant", fetch = FetchType.LAZY)
    private List<Order> orders = new ArrayList<>();

    @OneToMany(mappedBy = "restaurant", fetch = FetchType.LAZY)
    private List<RestaurantAuditReview> auditReviews;



    private Integer activeType;    //客户类型  潜在，成交，不活跃 CustomerActiveType
    // 分配，认领，投放公海，客户申请审核状态 等等， 记录最后一次的申请或者审核状态  RestaurantAuditShowStatus
    private Integer auditShowStatus;

    private Integer cooperatingState;    //合作状态 正常，跟进，搁置
    private String concern;    //客户关心点
    private String opponent;    //竞争对手
    private String specialReq;    //特殊需求
    private Integer stockRate;//进货频率

    private String receiver2; //联系人2
    private String telephone2; //联系人电话2
    private String receiver3; //联系人3
    private String telephone3; //联系人电话3


    @ManyToOne
    private AdminUser statusLastOperater;
    private Date statusLastOperateTime;

    @Temporal(TemporalType.TIMESTAMP)
    private Date createTime;
    @ManyToOne
    private AdminUser createOperater; //创建人

    @Temporal(TemporalType.TIMESTAMP)
    private Date lastOperateTime;
    @ManyToOne
    private AdminUser lastOperater; //最后修改人

    @Override
    public String toString() {
        return "Restaurant{" +
                "openWarning=" + openWarning +
                ", id=" + id +
                ", name='" + name + '\'' +
                ", address=" + address +
                ", license='" + license + '\'' +
                ", status=" + status +
                ", receiver='" + receiver + '\'' +
                ", telephone='" + telephone + '\'' +
                ", type=" + type +
                ", customer=" + customer +
                ", createTime=" + createTime +
                ", grade=" + grade +
                ", lastPurchaseTime=" + lastPurchaseTime +
                ", warning=" + warning +
                '}';
    }
}
