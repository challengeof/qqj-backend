package com.qqj.weixin.controller;

import com.qqj.request.PageRequest;
import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

@Getter
@Setter
public class WeixinUserListRequest extends PageRequest {

    private String nickname;

    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm")
    private Date beginTime;

    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm")
    private Date endTime;

}
