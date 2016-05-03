package com.qqj.weixin.controller;

import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

@Data
public class WeixinUserRequest {
    private Long id;

    private Short status;

    private String name;

    private String height;

    private String city;

    private String wechat;

    private String blog;

    private String userId;

    private String telephone;

    @DateTimeFormat(pattern="yyyy-MM-dd")
    private Date birthday;

    private ServerIds serverIds;

    private String openId;

    private String code;

}
