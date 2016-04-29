package com.qqj.weixin.facade;

import com.qqj.response.query.QueryResponse;
import com.qqj.response.query.WeixinUserStatisticsResponse;
import com.qqj.weixin.controller.WeixinUserListRequest;
import com.qqj.weixin.controller.WeixinUserRequest;
import com.qqj.weixin.domain.WeixinUser;
import com.qqj.weixin.enumeration.WeixinUserStatus;
import com.qqj.weixin.service.WeixinUserService;
import com.qqj.weixin.wrapper.WeixinUserWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class WeixinFacade {

    @Autowired
    private WeixinUserService weixinUserService;

    public QueryResponse<WeixinUserWrapper> getWeixinUserList(final WeixinUserListRequest request) {
        return weixinUserService.getWeixinUserList(request);
    }

    public void auditWeixinUser(Long id, Short status) {
        weixinUserService.auditWeixinUser(id, status);
    }

    public WeixinUserStatisticsResponse weixinUserStatistics() {
        return weixinUserService.weixinUserStatistics();
    }

    public void addWeixinUser(WeixinUserRequest request) {
        String accessToken = request.getAccessToken();
        WeixinUser weixinUser = new WeixinUser();
        weixinUser.setStatus(WeixinUserStatus.STATUS_0.getValue());
        weixinUser.setTelephone(request.getTelephone());
        weixinUser.setBirthday(request.getBirthday());
        weixinUser.setCreateTime(new Date());
        weixinUser.setName(request.getName());
        weixinUser.setOpenId(request.getOpenId());
        weixinUserService.addWeixinUser(request);
    }
}
