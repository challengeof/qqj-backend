package com.mishu.cgwy.common.facade;

import com.mishu.cgwy.admin.service.AdminUserService;
import com.mishu.cgwy.common.domain.City;
import com.mishu.cgwy.common.service.LocationService;
import com.mishu.cgwy.common.wrapper.CityWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
public class LocationFacade {
    @Autowired
    private LocationService locationService;

    @Autowired
    private AdminUserService adminUserService;



    public CityWrapper getCity(Long id) {
        return new CityWrapper(locationService.getCity(id));
    }

    @Transactional
    public CityWrapper saveCity(Long id, String name) {
        City city = null;
        if (null != id) {
            city = locationService.getCity(id);
        } else {
            city = new City();
        }
        city.setName(name);
        return new CityWrapper(locationService.saveCity(city));
    }

    public List<City> getAllCities() {
        return locationService.getAllCities();
    }

    public List<CityWrapper> getAllCityWrappers() {
        List<CityWrapper> cities = new ArrayList<>();
        for(City city : locationService.getAllCities()) {
            cities.add(new CityWrapper(city));
        }
        return cities;
    }


}
