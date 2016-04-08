package com.mishu.cgwy.coupon.repository;

import com.mishu.cgwy.coupon.domain.Coupon;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

/**
 * Created by bowen on 15-6-23.
 */
public interface CouponRepository extends JpaRepository<Coupon,Long>,JpaSpecificationExecutor<Coupon>{
}
