package com.qqj.weixin.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Created by wangguodong on 16/4/29.
 */
@Controller
public class WeixinUserController {
    @RequestMapping(value = "/api/weixin/user/add", method = RequestMethod.POST)
    @ResponseBody
    public void addWeixinUser(@RequestBody WeixinUserRequest request) {
    }
}
