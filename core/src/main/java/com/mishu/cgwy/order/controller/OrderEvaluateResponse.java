package com.mishu.cgwy.order.controller;

import java.util.ArrayList;
import java.util.List;

import lombok.Data;
@Data
public class OrderEvaluateResponse {
	private List<OrderEvaluateWrapper> orderEvaluates = new ArrayList<OrderEvaluateWrapper>();

	private long total;

	private int page;
	private int pageSize;

}
