package com.mishu.cgwy.app.dto;

import java.util.List;

import lombok.Data;

@Data
public class UncheckRestaurantResponse {

	/** 餐馆 */
	private List<RestaurantResponse> newRestaurant;
	/** 用户 */
	private List<UserResponse> newUser;
}
