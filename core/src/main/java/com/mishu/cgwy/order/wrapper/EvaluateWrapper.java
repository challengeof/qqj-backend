package com.mishu.cgwy.order.wrapper;

import lombok.Data;

import com.mishu.cgwy.order.domain.Evaluate;
@Data
public class EvaluateWrapper {
	private int productQualityScore;
	private int deliverySpeedScore;
	private int trackerServiceScore;
	private String msg;
	
	public EvaluateWrapper(){
		
	}
	public EvaluateWrapper(Evaluate evaluate){
		this.productQualityScore = evaluate.getProductQualityScore();
		this.trackerServiceScore = evaluate.getTrackerServiceScore();
		this.deliverySpeedScore = evaluate.getDeliverySpeedScore();
		this.msg = evaluate.getMsg();
	}

}
