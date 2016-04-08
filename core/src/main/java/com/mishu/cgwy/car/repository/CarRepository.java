package com.mishu.cgwy.car.repository;

import com.mishu.cgwy.car.domain.Car;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

/**
 * Created by bowen on 15-5-25.
 */
public interface CarRepository extends JpaRepository<Car, Long>, JpaSpecificationExecutor<Car> {

    public List<Car> findByDepotId(Long id);

    public Car findByAdminUserId(Long id);

}
