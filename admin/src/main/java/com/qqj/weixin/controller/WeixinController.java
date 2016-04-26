package com.qqj.weixin.controller;

import com.qqj.response.query.QueryResponse;
import com.qqj.weixin.facade.WeixinFacade;
import com.qqj.weixin.wrapper.WeixinUserWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * User: guodong
 */
@Controller
public class WeixinController {

    @Autowired
    WeixinFacade weixinFacade;

    @RequestMapping(value = "/api/weixin/user/list", method = {RequestMethod.GET})
    @ResponseBody
    public QueryResponse<WeixinUserWrapper> getWeixinUserList(WeixinUserListRequest weixinUserListRequest) {
        return weixinFacade.getWeixinUserList(weixinUserListRequest);
    }
}
