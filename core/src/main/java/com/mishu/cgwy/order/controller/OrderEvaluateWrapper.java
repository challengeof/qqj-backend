package com.mishu.cgwy.order.controller;

import com.mishu.cgwy.order.domain.Evaluate;
import com.mishu.cgwy.score.Wrapper.ScoreLogWrapper;
import lombok.Data;

import java.util.Date;
@Data
public class OrderEvaluateWrapper {
	private String adminUserName;
	
	private String trackerName;
	
	private Long orderId;
	
	private Date submitDate;
	
	private int productQualityScore;
	private int deliverySpeedScore;
	private int trackerServiceScore;
	
	private String msg;

	private ScoreLogWrapper scoreLog;
	public OrderEvaluateWrapper(){
		
	}
	
	public OrderEvaluateWrapper(Evaluate evaluate) throws Exception {
		if(evaluate.getAdminUser() != null){
			this.adminUserName = evaluate.getAdminUser().getRealname();
		}
		if(evaluate.getTracker() != null){
			this.trackerName = evaluate.getTracker().getRealname();
		}
		
		if(evaluate.getOrder() != null){
			this.orderId = evaluate.getOrder().getId();
			this.submitDate = evaluate.getOrder().getSubmitDate();
		}
		
		this.deliverySpeedScore = evaluate.getDeliverySpeedScore();
		this.msg = evaluate.getMsg();
		this.trackerServiceScore = evaluate.getTrackerServiceScore();
		this.productQualityScore = evaluate.getProductQualityScore();
		if(evaluate.getScoreLog()!=null){
			this.scoreLog=new ScoreLogWrapper(evaluate.getScoreLog());
		}
	}

}
