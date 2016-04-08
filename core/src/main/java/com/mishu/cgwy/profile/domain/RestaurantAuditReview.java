package com.mishu.cgwy.profile.domain;

import com.mishu.cgwy.admin.domain.AdminUser;
import lombok.Data;

import javax.persistence.*;
import java.util.Date;

/**
 * Created by king-ck on 2016/3/10.
 */
@Entity
@Data
public class RestaurantAuditReview {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "restaurant_id")
    private Restaurant restaurant;

    @ManyToOne
    @JoinColumn(name = "operater_user_id")
    private AdminUser operater; // 操作人
    private Integer status; // RestaurantReviewStatus  通过 驳回
    private Date operateTime; // 审核时间


    @ManyToOne
    @JoinColumn(name = "create_user_id")
    private AdminUser createUser;

    private Integer reqType; //申请审核类型  RestaurantAuditReviewType
    private Date createTime;

}
