package com.mishu.cgwy.push.controller;

import com.baidu.yun.push.exception.PushClientException;
import com.baidu.yun.push.exception.PushServerException;
import com.mishu.cgwy.push.facade.DailyPushFacade;
import com.mishu.cgwy.push.request.DailyPushRequest;
import com.mishu.cgwy.push.request.PrecisePushRequest;
import com.mishu.cgwy.push.service.PrecisePushService;
import com.mishu.cgwy.push.wrapper.DailyPushWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Created by bowen on 15-7-28.
 */
@Controller
public class PushController {

    @Autowired
    private PrecisePushService precisePushService;
    @Autowired
    private DailyPushFacade dailyPushFacade;

    @RequestMapping(value = "api/push/precise/create", method = RequestMethod.POST)
    @ResponseBody
    public void createPrecisePush(@RequestBody PrecisePushRequest request) throws PushClientException, PushServerException {
        precisePushService.precisePush(request);
    }

    @RequestMapping(value = "api/push/daily/update", method = RequestMethod.POST)
    @ResponseBody
    public DailyPushWrapper updateDailyPush(@RequestBody DailyPushRequest request) {
        return dailyPushFacade.updateDailyPush(request);
    }

    @RequestMapping(value = "api/push/daily/{id}", method = RequestMethod.GET)
    @ResponseBody
    public DailyPushWrapper getDailyPushById(@PathVariable Long id) {
        return dailyPushFacade.getDailyPushById(id);
    }

    @RequestMapping(value = "api/push/daily/{id}", method = RequestMethod.DELETE)
    @ResponseBody
    public void deleteDailyPushById(@PathVariable Long id) {
        dailyPushFacade.deleteDailyPushById(id);
    }

    @RequestMapping(value = "api/push/daily/list", method = RequestMethod.GET)
    @ResponseBody
    public List<DailyPushWrapper> getDailyPushList() {
        return dailyPushFacade.getBrandList();
    }
}
