package com.mishu.cgwy.message;

import java.io.Serializable;
import java.util.List;

import lombok.Data;

@Data
public class PromotionMessage implements Serializable {

	public PromotionMessage(CouponSenderEnum couponSenderEnum) {
		this.couponSenderEnum = couponSenderEnum;
	}

	private static final long serialVersionUID = 6511373205709026894L;

	private Long cityId;
	
	private Long warehouseId;

	private Long restaurantId;
	
	private Long customerId;
	
	private Long orderId;

	private Integer promotionType;

	private Long couponId;

	private CouponSenderEnum couponSenderEnum;

	private List<Long> stockOutIds;
}
