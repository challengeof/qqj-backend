package com.mishu.cgwy.app.dto;

import lombok.Data;

@Data
public class UserCheckRestaurantRequest {

	/** 餐馆id */
	private long restaurantId;
	/** 用户id */
	private long userId;
}
