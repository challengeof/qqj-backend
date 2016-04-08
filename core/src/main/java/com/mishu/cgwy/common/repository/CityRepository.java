package com.mishu.cgwy.common.repository;

import com.mishu.cgwy.common.domain.City;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;


/**
 * User: xudong
 * Date: 12/2/14
 * Time: 3:26 PM
 */
public interface CityRepository extends JpaRepository<City, Long> {
    List<City> findByName(String name);
}
