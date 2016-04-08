package com.mishu.cgwy.salesPerformance.repository;

import com.mishu.cgwy.salesPerformance.domain.RestaurantSalesPerformance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Date;
import java.util.List;

/**
 * Created by xiao1zhao2 on 15/12/15.
 */
public interface RestaurantSalesPerformanceRepository extends JpaRepository<RestaurantSalesPerformance, Long>, JpaSpecificationExecutor<RestaurantSalesPerformance> {
    List<RestaurantSalesPerformance> getByRestaurantIdAndDate(Long restaurantId, Date date);
}
