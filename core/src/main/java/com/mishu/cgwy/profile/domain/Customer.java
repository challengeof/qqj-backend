package com.mishu.cgwy.profile.domain;

import com.mishu.cgwy.admin.domain.AdminUser;
import com.mishu.cgwy.common.domain.Block;
import com.mishu.cgwy.common.domain.City;
import com.mishu.cgwy.score.domain.Score;
import com.mishu.cgwy.score.domain.ScoreLog;
import lombok.Data;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

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
    @JoinColumn(name = "block_id")
    private Block block;

    @ManyToOne
    @JoinColumn(name = "admin_user_id")
    private AdminUser adminUser;  //销售维护人员

    private Date adminUserFollowBegin;  //销售维护人员跟进开始时间
    private Date adminUserFollowEnd;  //销售维护人员跟进结束时间

    @ManyToOne
    @JoinColumn(name = "dev_user_id")
    private AdminUser devUser;    //销售开发人员
    

    private Integer followUpStatus; //跟进状态


//  private Integer createMode;// 创建方式 CustomerCreateModeEnum

    @Temporal(TemporalType.TIMESTAMP)
    private Date createTime;

    @Temporal(TemporalType.TIMESTAMP)
    private Date lastLoginTime;



    @OneToOne( fetch = FetchType.LAZY, mappedBy = "customer")
    private Score score;


    @OneToMany(mappedBy = "customer")
    private List<Restaurant> restaurant;

    @Transient
    public String getUserNumber() {
        return String.valueOf(id);
    }

    @Override
    public String toString() {
        return "Customer{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", password='" + password + '\'' +
                ", telephone='" + telephone + '\'' +
                ", enabled=" + enabled +
                ", createTime=" + createTime +
                ", lastLoginTime=" + lastLoginTime +
                '}';
    }
}
