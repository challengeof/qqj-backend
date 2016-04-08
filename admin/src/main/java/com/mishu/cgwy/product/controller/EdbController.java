package com.mishu.cgwy.product.controller;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.mishu.cgwy.product.facade.ProductEdbFacade;

@Controller
public class EdbController {

    @InitBinder
    public void initBinder(WebDataBinder binder) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        dateFormat.setLenient(false);
        binder.registerCustomEditor(Date.class, new CustomDateEditor(dateFormat, false));
    }
    
    @Autowired
    private ProductEdbFacade productEdbFacade; 
    
    @RequestMapping(value = "/api/synchroEdbData", method = RequestMethod.GET)
    @ResponseBody
    public String synchroEdbData(@RequestParam(value = "date", required = true) Date date) {
    	productEdbFacade.synchroEdbData(date);
    	return "success";
    }
}
