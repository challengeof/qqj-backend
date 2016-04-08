package com.mishu.cgwy.message;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Session;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;

import com.mishu.cgwy.coupon.constant.CouponConstant;

public class PromotionMessageSender {
	
    private final static Logger logger = LoggerFactory.getLogger(PromotionMessageSender.class);   

	@Autowired
	private JmsTemplate activityCouponMessagejmsTemplate;
	
	@Autowired
	private JmsTemplate defaultCouponMessagejmsTemplate;
	
	public void sendMessage(final PromotionMessage message) {
		logger.info(String.format("Message being sent to activemq: %s", message));
		
		try {
			MessageCreator messageCreator = new MessageCreator() {
				public Message createMessage(Session session) throws JMSException {
					return session.createObjectMessage(message);
				}
			};
			
			if (CouponConstant.ACTIVITY_SEND.equals(message.getPromotionType())) {
				activityCouponMessagejmsTemplate.send(messageCreator);
			} else {
				defaultCouponMessagejmsTemplate.send(messageCreator);
			}
		} catch (Exception e) {
			logger.error(String.format("Message sending failure: %s %s", e.getMessage(), message), e);
		}
	}
}
