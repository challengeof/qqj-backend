package com.mishu.cgwy.admin.controller;

import com.mishu.cgwy.coupon.service.CouponService;
import com.mishu.cgwy.message.PromotionMessageSender;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class MessageController {

    @Autowired
    private PromotionMessageSender promotionMessageSender;

	@Autowired
	private CouponService couponService;
    
    @Secured("ROLE_ADMIN")
    @RequestMapping(value = "/api/coupon/send-message", method = RequestMethod.GET)
    @ResponseBody
    public String sendMessage(
    		@RequestParam(value="couponId", required=true) Long couponId,
    		@RequestParam(value="cityId", required=false) Long cityId,
    		@RequestParam(value="warehouseId", required=false) Long warehouseId,
    		@RequestParam(value="customerId", required=false) Long customerId
    		) {
    	return couponService.sendCoupon(couponId, cityId, warehouseId, customerId);
    }
}
