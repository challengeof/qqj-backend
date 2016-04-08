package com.mishu.cgwy.schedule.controller;

import com.mishu.cgwy.schedule.service.ScheduleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
public class ScheduleController {

    @Autowired
    private ScheduleService scheduleService;

    @Secured("ROLE_ADMIN")
    @RequestMapping(value = "/api/schedule/customer-order-analysis", method = RequestMethod.GET)
    @ResponseBody
    public void customerOrderAnalysis() {
        scheduleService.customerOrderAnalysis();
    }
}