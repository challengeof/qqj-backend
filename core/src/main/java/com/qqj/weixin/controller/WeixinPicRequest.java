package com.qqj.weixin.controller;

import lombok.Data;

@Data
public class WeixinPicRequest {
    private String openId;
    private Short type;
    private String serverId;
}
