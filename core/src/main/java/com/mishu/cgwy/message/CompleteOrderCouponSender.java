package com.mishu.cgwy.message;

import com.mishu.cgwy.coupon.constant.CouponConstant;
import com.mishu.cgwy.coupon.constant.ShareTypeEnum;
import com.mishu.cgwy.coupon.domain.Coupon;
import com.mishu.cgwy.coupon.domain.Share;
import com.mishu.cgwy.coupon.service.ShareService;
import com.mishu.cgwy.order.domain.Order;
import com.mishu.cgwy.profile.domain.Customer;
import org.apache.commons.lang.time.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Calendar;
import java.util.Date;

@Service
public class CompleteOrderCouponSender extends CouponSender {

    @Autowired
    ShareService shareService;

    private static Long firstOrder = new Long(1);

	@Transactional
    public void send(PromotionMessage promotionMessage) {
        send(orderService.getOrderById(promotionMessage.getOrderId()));
    }

    public void send(Order order) {
        Date date = order.getSubmitDate();
        Date start = new Date();

        // TODO add complete date in order

        Customer customer = order.getCustomer();
        for (Coupon coupon : couponService.getAvailableOrderGift(CouponConstant.ORDER_WITH_A_GIFT_SEND.getType(), order, date)) {
            sendCouponToCustomer(customer, coupon, start, coupon.getDeadline(), false);
        }

        for (Coupon coupon : couponService.getAvailableOrderGift(CouponConstant.ORDER_WITH_A_COUPON_SEND.getType(), order, date)) {
            Date end = DateUtils.truncate(DateUtils.addDays(start, 8), Calendar.DAY_OF_MONTH);
            if (coupon.getPeriodOfValidity() != null) {
                end = DateUtils.truncate(DateUtils.addDays(start, coupon.getPeriodOfValidity()), Calendar.DAY_OF_MONTH);
            }
            sendCouponToCustomer(customer, coupon, start, end, false);
        }

        for (Coupon coupon : couponService.getAvailableOrderGift(CouponConstant.TWO_FOR_ONE.getType(), order, date)) {

            for (int i = 0; i < coupon.getSendCouponQuantity(); i++) {

                sendCouponToCustomer(customer, coupon, start, coupon.getDeadline(), false);
            }
        }

        if (firstOrder.equals(order.getSequence())) {
            Share share = shareService.findShare(customer, false, ShareTypeEnum.coupon);
            if (share != null) {
                Date end = DateUtils.truncate(DateUtils.addDays(start, 8), Calendar.DAY_OF_MONTH);

                for (Coupon coupon : couponService.getAvailableShareCoupon(share.getReference(), date)) {
                    sendCouponToCustomer(share.getReference(), coupon, start, end, false);
                }
            }
        }

    }
}
