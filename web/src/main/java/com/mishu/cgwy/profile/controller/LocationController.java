package com.mishu.cgwy.profile.controller;

import com.mishu.cgwy.common.domain.City;
import com.mishu.cgwy.common.facade.LocationFacade;
import com.mishu.cgwy.common.wrapper.CityWrapper;
import com.mishu.cgwy.common.wrapper.SimpleCityWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
import java.util.List;

/**
 * User: xudong
 * Date: 6/5/15
 * Time: 9:15 PM
 */
@Controller
public class LocationController {

    @Autowired
    private LocationFacade locationFacade;
    @RequestMapping("/api/v2/city")
    @ResponseBody
    public List<CityWrapper> getCities() {
        List<CityWrapper> cities = new ArrayList<>();
        for(City city: locationFacade.getAllCities()) {
            cities.add(new CityWrapper(city));
        }

        return cities;
    }
}
