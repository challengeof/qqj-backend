package com.qqj.weixin.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * User: guodong
 */
@Controller
public class CheckController {
    @RequestMapping(value = "/api/available",
            method = {
                    RequestMethod.GET,
                    RequestMethod.HEAD,
                    RequestMethod.POST,
                    RequestMethod.PUT,
                    RequestMethod.PATCH,
                    RequestMethod.DELETE,
                    RequestMethod.OPTIONS,
                    RequestMethod.TRACE,
            })
    @ResponseBody
    public void adminAvailable() {
        System.out.println("It's Ok.");
    }
}
