package com.qqj.weixin.controller;

import com.qqj.request.PageRequest;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class WeixinUserListRequest extends PageRequest {

    private Short group;

    private Short status;

    private String telephone;

}
