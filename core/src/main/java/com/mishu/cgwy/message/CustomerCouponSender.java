package com.mishu.cgwy.message;

import com.mishu.cgwy.coupon.constant.CouponConstant;
import com.mishu.cgwy.coupon.domain.Coupon;
import org.apache.commons.lang.time.DateUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Calendar;
import java.util.Date;

@Service
public class CustomerCouponSender extends CouponSender {

	@Transactional
    public void send(PromotionMessage promotionMessage) {
        Coupon coupon = couponRepository.getOne(promotionMessage.getCouponId());

        Date start;
        Date end;

        if (!CouponConstant.ACTIVITY_SEND.getType().equals(promotionMessage.getPromotionType())) {
            start = new Date();
            if (coupon.getDeadline() != null) {
                end = coupon.getDeadline();
            } else {
                end = DateUtils.truncate(DateUtils.addDays(start, 8), Calendar.DAY_OF_MONTH);
            }
        } else {
            start = coupon.getStart();
            end = coupon.getEnd();
        }

        sendCouponToCustomer(customerService.getCustomerById(promotionMessage.getCustomerId()), coupon, start, end, false);
    }
}
