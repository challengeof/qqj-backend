package com.qqj.weixin.controller;

import com.qqj.weixin.facade.WeixinFacade;
import com.qqj.weixin.wrapper.WeixinUserWrapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

/**
 * Created by wangguodong on 16/4/29.
 */
@Controller
public class WeixinUserController {

    Logger logger = LoggerFactory.getLogger(WeixinUserController.class);

    @Autowired
    private WeixinFacade weixinFacade;

    @RequestMapping(value = "/api/weixin/user/openId", method = RequestMethod.POST)
    @ResponseBody
    public WeixinUserWrapper getWeixinUserOpenId(@RequestBody WeixinUserRequest request) throws Exception {
        return weixinFacade.getWeixinUserOpenId(request.getCode());
    }

    @RequestMapping(value = "/api/weixin/user/status", method = RequestMethod.POST)
    @ResponseBody
    public WeixinUserWrapper getWeixinUserStatus(@RequestBody WeixinUserRequest request) throws Exception {
        return weixinFacade.getWeixinUserStatus(request.getOpenId());
    }

    @RequestMapping(value = "/api/weixin/user/upload-pic", method = RequestMethod.POST)
    @ResponseBody
    public UploadPicResponse uploadPic(@RequestBody WeixinPicRequest request) throws Exception {
        return weixinFacade.uploadPic(request);
    }

    @RequestMapping(value = "/api/weixin/user/add", method = RequestMethod.POST)
    @ResponseBody
    public void addWeixinUser(@RequestBody WeixinUserRequest request) {
        try {
            weixinFacade.addWeixinUser(request);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
    }

    @RequestMapping(value = "/api/weixin/user/{openId}", method = RequestMethod.GET)
    @ResponseBody
    public WeixinUserWrapper getWeixinUser(@PathVariable("openId") String openId) {
        return weixinFacade.getWeixinUser(openId);
    }
}
