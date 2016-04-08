package com.mishu.cgwy.workTicket.domain;

import com.mishu.cgwy.admin.domain.AdminUser;
import com.mishu.cgwy.order.domain.Order;
import com.mishu.cgwy.profile.domain.Restaurant;
import lombok.Data;

import javax.persistence.*;
import java.util.Date;

/**
 * Created by bowen on 16/2/29.
 */
@Entity
@Data
public class WorkTicket {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String consultants;

    private String consultantsTelephone;

    private String username;

    @ManyToOne
    @JoinColumn(name = "restaurant_id")
    private Restaurant restaurant;

    @ManyToOne
    @JoinColumn(name = "order_id")
    private Order order;

    @ManyToOne
    @JoinColumn(name = "admin_user_id")
    private AdminUser followUp;

    private Integer problemSources;

    private Integer process;

    @Column(columnDefinition = "text")
    private String content;

    private int status;

    private Date createTime;

    @ManyToOne
    @JoinColumn(name = "operator")
    private AdminUser operator;

    @Column(columnDefinition = "text")
    private String operateLog;
}
