package com.mishu.cgwy.log;

import org.apache.commons.lang.ArrayUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Created by wangwei on 15/11/24.
 */
public class LogConfig {

    private Logger logger = LoggerFactory.getLogger(LogConfig.class);

    public Object printArgs(ProceedingJoinPoint jp) throws Exception {
        Long before = System.currentTimeMillis();
        logger.info(jp.getSignature().toString());
        logger.info(ArrayUtils.toString(jp.getArgs()));
        try {
            return jp.proceed();
        } catch (Throwable e) {
            logger.error(e.getMessage(), e);
            throw new Exception(e);
        } finally {
            long time = System.currentTimeMillis() - before;
            if (time >= 1000) {
                logger.info(jp.getSignature().toString() + ",run about " + time / 1000 + " second," + time + " milliseconds");
            }
        }
    }
}
