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
    @JoinColumn(name = "open_id")
    private WeixinUser user;

    private String url;

    //个人图片编号
    private Short seq;

    @Temporal(TemporalType.TIMESTAMP)
    private Date createTime;
}
