package com.mishu.cgwy.device.controller;

import com.mishu.cgwy.device.DeviceMappingService;
import com.mishu.cgwy.profile.controller.CurrentCustomer;
import com.mishu.cgwy.profile.domain.Customer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * User: xudong
 * Date: 6/28/15
 * Time: 9:40 PM
 */
@Controller
public class DeviceMappingController {

    private static Logger logger = LoggerFactory.getLogger(DeviceMappingController.class);
    @Autowired
    private DeviceMappingService deviceMappingService;

    @RequestMapping(value = "/api/v2/device", method = RequestMethod.PUT)
    @ResponseBody
    public void bindDevice(@CurrentCustomer Customer customer, @RequestParam("platform") String platform,
                           @RequestParam("deviceId") String deviceId) {
        Long customerId = null;
        if (customer != null) {
            customerId = customer.getId();
        }

        logger.info("bindDevice platform " + platform + " deviceId " + deviceId + " customerId " + customerId);
        deviceMappingService.saveDeviceMapping(customerId, platform, deviceId);
    }

}
