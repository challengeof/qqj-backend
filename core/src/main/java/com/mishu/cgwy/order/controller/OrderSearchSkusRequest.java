package com.mishu.cgwy.order.controller;

import java.util.Date;

import com.mishu.cgwy.organization.domain.Organization;
import lombok.Data;

@Data
public class OrderSearchSkusRequest {
	private Long skuId;
	private Date expectedArrivedDate;
	private Long warehouseId;

	private Long cityId;

	private Long organizationId;


}
