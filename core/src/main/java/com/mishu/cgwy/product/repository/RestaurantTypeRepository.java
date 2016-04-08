package com.mishu.cgwy.product.repository;

import com.mishu.cgwy.profile.domain.Restaurant;
import com.mishu.cgwy.profile.domain.RestaurantType;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

/**
 * Created by wangwei on 15/9/16.
 */
public interface RestaurantTypeRepository extends JpaRepository<RestaurantType, Long>, JpaSpecificationExecutor<RestaurantType> {

    List<RestaurantType> findByParentRestaurantTypeIsNull();

}
