package com.mishu.cgwy.order.controller;

import java.util.ArrayList;
import java.util.List;

import lombok.Data;


@Data
public class SkuSaleResponse {
	private List<SkuSaleWrapper> skuSales = new ArrayList<SkuSaleWrapper>();
	private int page;
	private int pageSize;
	
	private long total;

}
