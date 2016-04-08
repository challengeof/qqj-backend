package com.mishu.cgwy.carRoute.repository;

import com.mishu.cgwy.carRoute.domain.CarRoute;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

/**
 * Created by xgl on 2016/04/05.
 */
public interface CarRouteRepository extends JpaRepository<CarRoute, Long>,JpaSpecificationExecutor<CarRoute> {

}
