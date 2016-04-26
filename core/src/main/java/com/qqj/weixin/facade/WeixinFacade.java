package com.qqj.weixin.facade;

import com.qqj.response.query.QueryResponse;
import com.qqj.weixin.controller.WeixinUserListRequest;
import com.qqj.weixin.service.WeixinUserService;
import com.qqj.weixin.wrapper.WeixinUserWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class WeixinFacade {

    @Autowired
    private WeixinUserService weixinUserService;

    public QueryResponse<WeixinUserWrapper> getWeixinUserList(final WeixinUserListRequest request) {
        return weixinUserService.getWeixinUserList(request);
    }
}
