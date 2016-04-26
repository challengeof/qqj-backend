package com.qqj.weixin.domain;

import lombok.Data;

import javax.persistence.*;
import java.util.Date;

/**
 * Created by wangguodong on 16/4/26.
 */
@Entity
@Data
public class WeixinPic {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "weixin_user_id")
    private WeixinUser user;

    private String qiNiuHash;

    //个人图片编号
    private Short type;

    @Temporal(TemporalType.TIMESTAMP)
    private Date createTime;
}
