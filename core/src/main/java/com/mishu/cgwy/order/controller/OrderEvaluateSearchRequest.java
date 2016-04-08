package com.mishu.cgwy.order.controller;

import java.util.Date;

import lombok.Data;

@Data
public class OrderEvaluateSearchRequest {
	private Date start;

	private Date end;

	private Long orderId;

	private Long customerId;

	private Long adminUserId;

	private Long trackerId;

	private String trackerName;

	private String adminName;

	private String customerName;

	private Long cityId;

	private Long organizationId;

	private Long warehouseId;

	private boolean onlyNoScore;

	private int page = 0;

	private int pageSize = 100;

}
