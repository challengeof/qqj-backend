package com.mishu.cgwy.admin.controller;

import com.mishu.cgwy.common.facade.LocationFacade;
import com.mishu.cgwy.common.wrapper.CityWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Created by wangwei on 15/7/3.
 */
@Controller
public class CityController {

    @Autowired
    private LocationFacade locationFacade;

    @RequestMapping(value = "/api/city", method = RequestMethod.GET)
    @ResponseBody
    public List<CityWrapper> getCities() {
        return locationFacade.getAllCityWrappers();
    }

    @RequestMapping(value = "/api/city", method = RequestMethod.POST)
    @ResponseBody
    public CityWrapper createCity(@RequestParam("name") String name) {
        return locationFacade.saveCity(null, name);
    }

    @RequestMapping(value = "/api/city/{id}", method = RequestMethod.GET)
    @ResponseBody
    public CityWrapper getCity(@PathVariable("id") Long id) {
        return locationFacade.getCity(id);
    }

    @RequestMapping(value = "/api/city/{id}", method = RequestMethod.PUT)
    @ResponseBody
    public CityWrapper updateCity(@PathVariable("id") Long id, @RequestParam("name") String name) {
        return locationFacade.saveCity(id, name);
    }


}
