package com.mishu.cgwy.order.dto;

import lombok.Data;

@Data
public class CustomerEvaluateRequest {
	private boolean satisfied;
	private String evaluateStr;

}
