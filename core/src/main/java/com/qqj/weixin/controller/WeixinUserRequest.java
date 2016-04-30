package com.qqj.weixin.controller;

import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

@Data
public class WeixinUserRequest {
    private Long id;

    private Short status;

    private String name;

    private String telephone;

    @DateTimeFormat(pattern="yyyy-MM-dd")
    private Date birthday;

    private String[] serverIds;

    private String code;

    private String accessToken;

}
