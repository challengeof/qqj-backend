package com.mishu.cgwy.task.domain;

import com.mishu.cgwy.admin.domain.AdminUser;
import com.mishu.cgwy.common.domain.City;
import com.mishu.cgwy.order.domain.Order;
import com.mishu.cgwy.purchase.domain.PurchaseOrder;
import com.mishu.cgwy.stock.domain.Depot;
import com.mishu.cgwy.stock.domain.Transfer;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by guodong on 16/01/15.
 */
@Entity
@Getter
@Setter
public class Task {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private AdminUser submitUser;

    @Temporal(TemporalType.TIMESTAMP)
    private Date submitDate;

    @Column(columnDefinition = "text")
    private String taskCondition;

    private long timeCost;//in milliseconds

    private String result;

    private Short status;

    private Short type;

    private String description;

    private String remark;
}
