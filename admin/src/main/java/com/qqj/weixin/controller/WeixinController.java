package com.qqj.weixin.controller;

import com.qqj.response.query.QueryResponse;
import com.qqj.weixin.enumeration.WeixinUserGroup;
import com.qqj.weixin.enumeration.WeixinUserStatus;
import com.qqj.weixin.facade.WeixinFacade;
import com.qqj.weixin.wrapper.WeixinUserWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

/**
 * User: guodong
 */
@Controller
public class WeixinController {

    @Autowired
    WeixinFacade weixinFacade;

    @RequestMapping(value = "/weixin/user/list", method = {RequestMethod.GET})
    @ResponseBody
    public QueryResponse<WeixinUserWrapper> getWeixinUserList(WeixinUserListRequest weixinUserListRequest) {
        return weixinFacade.getWeixinUserList(weixinUserListRequest);
    }

    @RequestMapping(value = "/weixin/user/groups", method = RequestMethod.GET)
    @ResponseBody
    public WeixinUserGroup[] getWeixinUserGroups() {
        return WeixinUserGroup.values();
    }

    @RequestMapping(value = "/weixin/user/statuses", method = RequestMethod.GET)
    @ResponseBody
    public WeixinUserStatus[] getWeixinUserStatuses() {
        return WeixinUserStatus.values();
    }

    @RequestMapping(value = "/weixin/user/audit", method = {RequestMethod.POST})
    @ResponseBody
    public void auditWeixinUser(@RequestBody WeixinUserRequest request) {
        weixinFacade.auditWeixinUser(request.getId(), request.getStatus());
    }
}
