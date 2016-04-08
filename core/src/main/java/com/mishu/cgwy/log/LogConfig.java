package com.mishu.cgwy.log;

import com.mishu.cgwy.error.BusinessException;
import com.mishu.cgwy.error.UserDefinedException;
import org.apache.commons.lang.ArrayUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.orm.ObjectOptimisticLockingFailureException;

import javax.persistence.OptimisticLockException;


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
            if (e instanceof BusinessException) {
                throw (BusinessException)e;
            } else if (e instanceof OptimisticLockException || e instanceof ObjectOptimisticLockingFailureException) {
                throw new UserDefinedException("其他人正在操作同一张表,稍等再操作...");
            } else {
                throw new Exception(e);
            }
        } finally {
            long time = System.currentTimeMillis() - before;
            if (time >= 1000) {
                logger.info(jp.getSignature().toString() + ",run about " + time / 1000 + " second," + time + " milliseconds");
            }
        }
    }
}
