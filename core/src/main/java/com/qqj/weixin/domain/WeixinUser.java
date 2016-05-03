package com.qqj.weixin.domain;

import lombok.Data;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by wangguodong on 16/4/26.
 */
@Entity
@Data
@Cache(usage= CacheConcurrencyStrategy.READ_WRITE)
public class WeixinUser {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String openId;

    private String name;

    private String height;

    private String city;

    private String wechat;

    private String blog;

    private String userId;

    private String telephone;

    private Date birthday;

    private String nickname;

    @OneToMany(cascade = {CascadeType.ALL}, orphanRemoval = true)
    @JoinColumn(name = "weixin_user_id")
    private List<WeixinPic> pics = new ArrayList<WeixinPic>();

    @Temporal(TemporalType.TIMESTAMP)
    private Date createTime;

    @Temporal(TemporalType.TIMESTAMP)
    private Date auditTime;

    //审核状态
    private Short status = (short)0;
}
