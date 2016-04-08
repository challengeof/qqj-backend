package com.mishu.cgwy.lock.aop;

import com.mishu.cgwy.response.Response;
import org.aspectj.lang.ProceedingJoinPoint;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.orm.ObjectOptimisticLockingFailureException;

/**
 * Created by wangguodong on 15/12/4.
 */
public class AlertLockingFailureExceptionAspect {

    private static Logger logger = LoggerFactory.getLogger(AlertLockingFailureExceptionAspect.class);

    public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
        try {
            return joinPoint.proceed();
        } catch (ObjectOptimisticLockingFailureException e) {
            logger.error(e.getMessage(), e);
            Response response = new Response();
            response.setSuccess(Boolean.FALSE);
            response.setMsg("其他人正在操作，请勿重复操作。");
            return response;
        }
    }

}