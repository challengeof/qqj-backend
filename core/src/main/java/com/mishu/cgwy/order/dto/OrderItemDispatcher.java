package com.mishu.cgwy.order.dto;

import com.mishu.cgwy.product.domain.Sku;
import lombok.Data;

import com.mishu.cgwy.admin.domain.AdminUser;
import com.mishu.cgwy.inventory.domain.Vendor;
import com.mishu.cgwy.order.domain.OrderItem;
import com.mishu.cgwy.profile.domain.Restaurant;

@Data
public class OrderItemDispatcher {

    private int index = 0;

	private Sku sku;

    private int quantity = 0;
	
	private Restaurant restaurant;
	
	private AdminUser tracker;

	private Vendor vendor; 
	
	private Long orderId;
	
	private String expectedArrivedDate;
	
}
