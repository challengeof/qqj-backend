package com.mishu.cgwy.conf.controller;

import com.mishu.cgwy.admin.controller.CurrentAdminUser;
import com.mishu.cgwy.admin.domain.AdminUser;
import com.mishu.cgwy.conf.facade.ConfFacade;
import com.mishu.cgwy.conf.service.ConfService;
import com.mishu.cgwy.conf.vo.OrderLimit;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Created by Guodong.
 */
@Controller
public class ConfController {

    @Autowired
    private ConfService confService;

    @Autowired
    private ConfFacade confFacade;

    @RequestMapping(value = "/api/conf/orderLimit/list",method = RequestMethod.GET)
    @ResponseBody
    public List<OrderLimit> getOrderLimitList(@CurrentAdminUser AdminUser adminUser) throws Exception {
        return confFacade.getOrderLimitList(adminUser);
    }

    @RequestMapping(value = "/api/conf/save",method = RequestMethod.POST)
    @ResponseBody
    public void saveOrderLimitConf(@RequestBody SaveConfRequest saveConfRequest, @CurrentAdminUser AdminUser adminUser) throws Exception {
        confService.save(saveConfRequest);
    }
}
