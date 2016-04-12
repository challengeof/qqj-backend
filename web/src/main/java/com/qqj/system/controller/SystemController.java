package com.qqj.system.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * User: xudong
 * Date: 5/13/15
 * Time: 4:44 PM
 */
@Controller
public class SystemController {
    @RequestMapping("/api/system/param")
    @ResponseBody
    public SystemParamResponse systemParams() {
        return new SystemParamResponse();
    }
}
