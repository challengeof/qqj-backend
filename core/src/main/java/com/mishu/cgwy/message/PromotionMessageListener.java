package com.mishu.cgwy.message;

import javax.jms.Message;
import javax.jms.MessageListener;

import org.apache.activemq.command.ActiveMQObjectMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

public class PromotionMessageListener implements MessageListener {
	
	@Autowired
	CouponSenderFactory couponSenderFactory;
	
    private final static Logger logger = LoggerFactory.getLogger(PromotionMessageListener.class);   

	public void onMessage(Message message) {
		
		PromotionMessage promotionMessage = null;
		
		try {
			long start = System.currentTimeMillis();
			promotionMessage = (PromotionMessage) ((ActiveMQObjectMessage) message).getObject();
			couponSenderFactory.getCouponSender(promotionMessage.getCouponSenderEnum().getCouponSenderClass()).send(promotionMessage);
			
			long end = System.currentTimeMillis();
			
			logger.info(String.format("cost: %sms promotionMessage: %s", end - start, promotionMessage));
			
		} catch (Exception e) {
			logger.error(String.format("promotionMessage: %s errorMessage: %s", promotionMessage, e.getMessage()), e);
		}
	}
}
