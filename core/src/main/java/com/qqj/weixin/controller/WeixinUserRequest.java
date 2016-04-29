package com.qqj.weixin.controller;

import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

@Getter
@Setter
public class WeixinUserRequest {
    private Long id;

    private Short status;

    private String name;

    private String telephone;

    private String openId;

    private String accessToken;

    @DateTimeFormat(pattern="yyyy-MM-dd")
    private Date birthDay;

    private String serverId;

}
