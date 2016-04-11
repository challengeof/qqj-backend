package com.mishu.cgwy.profile.domain;

import com.mishu.cgwy.admin.domain.AdminUser;
import com.mishu.cgwy.common.domain.City;
import lombok.Data;

import javax.persistence.*;
import java.util.Date;

/**
 * User: xudong
 * Date: 2/27/15
 * Time: 7:23 PM
 */
@Entity
@Data
public class Customer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String username;

    private String password;

    private String telephone;

    private  Integer versionCode;

    private boolean enabled = true;

    @Transient
    private String userNumber;

    private Long referrerId;

    @ManyToOne
    @JoinColumn(name = "city_id")
    private City city;

    @ManyToOne
    @JoinColumn(name = "admin_user_id")
    private AdminUser adminUser;  //销售维护人员

    private Date adminUserFollowBegin;  //销售维护人员跟进开始时间
    private Date adminUserFollowEnd;  //销售维护人员跟进结束时间

    @ManyToOne
    @JoinColumn(name = "dev_user_id")
    private AdminUser devUser;    //销售开发人员
    

    private Integer followUpStatus; //跟进状态

    @Temporal(TemporalType.TIMESTAMP)
    private Date createTime;

    @Temporal(TemporalType.TIMESTAMP)
    private Date lastLoginTime;

    @Transient
    public String getUserNumber() {
        return String.valueOf(id);
    }

}
