package com.mishu.cgwy.profile.repository;

import com.mishu.cgwy.profile.domain.Restaurant;
import com.mishu.cgwy.profile.domain.RestaurantAlterLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;


/**
 * User: xudong
 * Date: 12/2/14
 * Time: 3:26 PM
 */
public interface RestaurantAlterLogRepository extends JpaRepository<RestaurantAlterLog, Long>, JpaSpecificationExecutor<RestaurantAlterLog> {

}
