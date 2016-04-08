package com.mishu.cgwy.coupon.repository;

import com.mishu.cgwy.coupon.domain.Coupon;
import com.mishu.cgwy.coupon.domain.CustomerCoupon;
import com.mishu.cgwy.profile.domain.Customer;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Date;
import java.util.List;

/**
 * Created by bowen on 15-6-24.
 */
public interface CustomerCouponRepository extends JpaRepository<CustomerCoupon,Long>,JpaSpecificationExecutor<CustomerCoupon> {
    
    List<CustomerCoupon> findByCustomerOrderByStatusAsc(Customer customer);
    
    List<CustomerCoupon> findByCustomerAndCoupon(Customer customer, Coupon coupon);

    @Modifying
    @Query("update CustomerCoupon c set c.status = :newStatus  where c.id = :customerCouponId and c.status=:oldStatus")
    int updateStatus(@Param("customerCouponId") long customerCouponId, @Param("oldStatus") int oldStatus, @Param("newStatus") int newStatus);

    @Modifying
    @Query("update CustomerCoupon c set c.status = :newStatus  where c.sendDate>:beginSend and  c.end<:now and c.status=:oldStatus")
    int updateStatusByExpire( @Param("oldStatus") int oldStatus, @Param("newStatus") int newStatus, @Param("now")Date now, @Param("beginSend")Date beginSend);
}
