package com.mishu.cgwy.message;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class MessageConsumer {
    private final static Logger logger = LoggerFactory.getLogger(MessageConsumer.class);   
	
	public static void main(String[] args) {
		new ClassPathXmlApplicationContext(new String[]{"application-persist.xml", "application-message.xml"});
		logger.info("Application started successfully.");
	}
}
