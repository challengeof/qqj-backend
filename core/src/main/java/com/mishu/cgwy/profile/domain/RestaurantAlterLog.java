package com.mishu.cgwy.profile.domain;

import com.mishu.cgwy.admin.domain.AdminUser;
import lombok.Data;

import javax.persistence.*;
import java.util.Date;

/**
 * Created by king-ck on 2016/2/29.
 */
@Entity
@Data
public class RestaurantAlterLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "restaurant_id")
    private Restaurant restaurant;

    @Lob
    private String val; //字段值变更记录

    @ManyToOne
    private AdminUser operater; //操作人
//    private Integer type; //此条记录的类型
    private Date createDate;


}
