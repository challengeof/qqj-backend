package com.mishu.cgwy.coupon.repository;

import com.mishu.cgwy.coupon.domain.Coupon;
import com.mishu.cgwy.coupon.domain.CustomerCoupon;
import com.mishu.cgwy.coupon.domain.Share;
import com.mishu.cgwy.profile.domain.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

/**
 * Created by wangguodong on 15/7/28.
 */
public interface ShareRepository extends JpaRepository<Share, Long>,JpaSpecificationExecutor<Share> {
    List<Share> findByRegistrantAndCouponSendedAndShareType(Customer registrant, Boolean couponSended, Integer shareType);
    List<Share> findByRegistrantAndShareType(Customer registrant, Integer shareType);
}
