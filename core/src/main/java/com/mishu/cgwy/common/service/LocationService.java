package com.mishu.cgwy.common.service;

import com.mishu.cgwy.common.domain.City;
import com.mishu.cgwy.common.repository.CityRepository;
import com.mishu.cgwy.error.CityAlreadyExistsException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * User: xudong
 * Date: 2/28/15
 * Time: 2:33 PM
 */
@Service
@Transactional
public class LocationService {
    @Autowired
    private CityRepository cityRepository;


    public List<City> getAllCities() {
        return cityRepository.findAll();
    }

    public City getCity(Long cityId) {
        return cityRepository.getOne(cityId);
    }

    public City saveCity(City city) {
        City tempCity = findCityByName(city.getName());
        if(tempCity != null && !city.getId().equals(tempCity.getId())){
            throw new CityAlreadyExistsException();
        }
        return cityRepository.save(city);
    }

    public void deleteCity(Long cityId) {
        cityRepository.delete(cityId);
    }

    @Transactional
    public City findCityByName(String name){
        List<City> cities = cityRepository.findByName(name);
        if(cities.isEmpty()){
            return null;
        }
        return cities.get(0);
    }


}
