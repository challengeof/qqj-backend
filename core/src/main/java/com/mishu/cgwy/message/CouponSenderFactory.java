package com.mishu.cgwy.message;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.stereotype.Component;

@Component
public class CouponSenderFactory implements BeanFactoryAware {
	
	private BeanFactory beanFactory = null;
	
	@Override
	public void setBeanFactory(BeanFactory factory) throws BeansException {
        this.beanFactory = factory;
    }
	
	public CouponSender getBean(Class<? extends CouponSender> cls) {
		return beanFactory.getBean(cls);
	}
	
	public CouponSender getCouponSender(Class<? extends CouponSender> cls) {
		return getBean(cls);
	}
}
