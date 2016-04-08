package com.mishu.cgwy.message;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.mishu.cgwy.coupon.domain.Coupon;
import com.mishu.cgwy.profile.domain.Customer;

@Service
public class ActivityCouponSender extends CouponSender {

	@Transactional
    public void send(PromotionMessage promotionMessage) {
		Long customerId = promotionMessage.getCustomerId();
        Long couponId = promotionMessage.getCouponId();
        Coupon coupon = couponRepository.getOne(couponId);

        List<Customer> customers = new ArrayList<>();
        if (customerId != null) {
            customers = Arrays.asList(customerService.getCustomerById(customerId));
        } else if (promotionMessage.getWarehouseId() != null) {
            customers = customerService.findCustomerByWarehouse(promotionMessage.getWarehouseId());
        }  else if (promotionMessage.getCityId() != null) {
            customers = customerService.findCustomerByCity(promotionMessage.getCityId());
        }

        for (Customer customer : customers) {
            sendCouponToCustomer(customer,  coupon, coupon.getStart(), coupon.getEnd(), true);
        }
	}

}
