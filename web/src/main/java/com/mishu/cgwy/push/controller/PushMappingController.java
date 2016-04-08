package com.mishu.cgwy.push.controller;

import com.mishu.cgwy.profile.controller.CurrentCustomer;
import com.mishu.cgwy.profile.domain.Customer;
import com.mishu.cgwy.push.service.PushMappingService;
import com.mishu.cgwy.push.facade.PushMappingFacade;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.IOException;

/**
 * User: xudong
 * Date: 6/28/15
 * Time: 9:40 PM
 */
@Controller
public class PushMappingController {

    @Autowired
    private PushMappingService pushMappingService;

    @Autowired
    private PushMappingFacade pushMappingFacade;

    @RequestMapping(value = "/api/v2/push", method = RequestMethod.PUT)
    @ResponseBody
    public void bindPush(@CurrentCustomer Customer customer, @RequestParam("baiduChannelId") String baiduChannelId,
                         @RequestParam(value = "platform", defaultValue = "android") String platform) {
        pushMappingService.savePushMapping(customer.getId(), baiduChannelId, platform);
    }


    @RequestMapping(value = "/api/v2/weixin", method = RequestMethod.PUT)
    @ResponseBody
    public void wxCode(@CurrentCustomer Customer customer,@RequestParam("code") String code, @RequestParam("state") String state) throws IOException {

        String openId = pushMappingFacade.getWxOAuth2Token(code);
        pushMappingService.savePushMapping(customer.getId(),openId,state);


    }


}
