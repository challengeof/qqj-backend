package com.mishu.cgwy.app.dto;

import lombok.Data;

@Data
public class IndexResponse {
	/** 今日配送 */
	private long deliver;
	/** 订单预警 */
	private long risk;
	/** 待审核 */ 
	//所有餐馆待审核（客服是自己）数量
	private long unAudit;
}
