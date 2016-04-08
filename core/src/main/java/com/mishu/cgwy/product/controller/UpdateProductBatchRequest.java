package com.mishu.cgwy.product.controller;

import java.util.ArrayList;
import java.util.List;

import lombok.Data;

@Data
public class UpdateProductBatchRequest {
	private List<Long> changeDetailIds = new ArrayList<Long>();
	
	private Long status;

}
