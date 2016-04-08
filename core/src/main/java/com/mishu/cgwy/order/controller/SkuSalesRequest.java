package com.mishu.cgwy.order.controller;

import java.util.Date;

import lombok.Data;

@Data
public class SkuSalesRequest {
	private Date start;
	private Date end;

	private String productName;

	private Long brandId;

	private Long warehouseId;

	private int page = 0;
	
	private int pageSize = 100;
	
	private String vendorId;

	private Long cityId;

	private Long organizationId;
}
