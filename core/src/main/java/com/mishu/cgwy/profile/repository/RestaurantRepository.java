package com.mishu.cgwy.profile.repository;

import com.mishu.cgwy.profile.domain.Restaurant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;
import org.springframework.data.repository.query.Param;

import java.util.List;


/**
 * User: xudong
 * Date: 12/2/14
 * Time: 3:26 PM
 */
public interface RestaurantRepository extends JpaRepository<Restaurant, Long>, JpaSpecificationExecutor<Restaurant> {
    List<Restaurant> findByCustomerId(Long customerId);

    Restaurant findById(Long id);

    @Modifying
    @Query("update Restaurant r set r.auditShowStatus=:val where r.id=:restaurantId")
    int updateAuditShowStatus(@Param("val")Integer val, @Param("restaurantId")Long restaurantId);


}
