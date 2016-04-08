package com.mishu.cgwy.check.controller;

import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;
import com.mishu.cgwy.product.domain.CategoryStatus;
import com.mishu.cgwy.product.facade.ProductFacade;
import com.mishu.cgwy.product.wrapper.CategoryWrapper;
import com.mishu.cgwy.utils.TreeJsonHasChild;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

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
    public void adminAvilable() {}
}
