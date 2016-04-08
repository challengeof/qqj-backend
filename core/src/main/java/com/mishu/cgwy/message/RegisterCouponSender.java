package com.mishu.cgwy.message;

import com.mishu.cgwy.coupon.domain.Coupon;
import com.mishu.cgwy.profile.domain.Customer;

import com.mishu.cgwy.profile.domain.Restaurant;
import org.apache.commons.lang.time.DateUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
public class RegisterCouponSender extends CouponSender {

	@Transactional
    public void send(PromotionMessage promotionMessage) {

        Restaurant restaurant = restaurantService.getOne(promotionMessage.getRestaurantId());
        Customer customer = restaurant.getCustomer();

        Date restaurantAuditTime = restaurant.getAuditTime();
        Date now = new Date();

        // 如果用户已经发送过注册优惠券，则在后续的注册活动中，不再发送。
        if (!couponService.existsRegisterCustomerCoupon(customer.getId())) {
            // 查询处于餐馆首次审核通过时间在注册活动时间范围内的注册优惠券。
            for (Coupon coupon : couponService.getAvailableRegisterCouponForNewCustomer(customer, restaurantAuditTime)) {
                Date start = DateUtils.truncate(DateUtils.addDays(new Date(), coupon.getBeginningDays()), Calendar.DAY_OF_MONTH);
                int periodOfValidity = coupon.getPeriodOfValidity() == null ? 14 : coupon.getPeriodOfValidity();
                Date end = DateUtils.truncate(DateUtils.addDays(start, periodOfValidity), Calendar.DAY_OF_MONTH);
                sendCouponToCustomer(customer, coupon, start, end,  true);
            }
        }

        for(Coupon coupon: couponService.getAvailableActivityCouponForNewCustomer(customer, now)) {
            sendCouponToCustomer(customer, coupon, coupon.getStart(), coupon.getEnd(), true);
        }
    }

}
