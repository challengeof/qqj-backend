package com.mishu.cgwy.order.controller;

import lombok.Data;

@Data
public class SkuSaleWrapper {
	
	private Long skuId;
	
	private String skuName;

	private Integer capacityInBundle = 1;
	
	private Long singleSale;

	private Long bundleSale;

	private Long countSale;

	private Long sellReturn;

	private Long sellCancel;
}
