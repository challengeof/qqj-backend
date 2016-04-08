package com.mishu.cgwy.order.controller;

import java.util.ArrayList;
import java.util.List;

import lombok.Data;

import com.mishu.cgwy.product.wrapper.SimpleSkuWrapper;
@Data
public class OrderSearchSkusResponse {
	private List<SimpleSkuWrapper> skus = new ArrayList<SimpleSkuWrapper>();

	
}
