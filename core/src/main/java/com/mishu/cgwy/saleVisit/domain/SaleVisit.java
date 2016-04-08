package com.mishu.cgwy.saleVisit.domain;

import com.mishu.cgwy.admin.domain.AdminUser;
import com.mishu.cgwy.profile.domain.Restaurant;
import lombok.Data;

import javax.persistence.*;
import java.util.Date;

/**
 * Created by wangwei on 15/8/13.
 */
@Entity
@Data
public class SaleVisit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "restaurant_id")
    private Restaurant restaurant;

    private Date visitTime;

    private Integer visitStage;

    private String visitPurposes;

    private String intentionProductions;

    private String visitTroubles;

    private String visitSolutions;

    private Date nextVisitTime;

    private Integer nextVisitStage;

    @Column(columnDefinition = "text")
    private String remark;

    @ManyToOne
    @JoinColumn(name = "admin_user_id")
    private AdminUser creator;

    @Column(name = "creat_time")
    private Date createTime;

}
