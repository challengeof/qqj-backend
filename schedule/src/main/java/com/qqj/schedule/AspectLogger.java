package com.qqj.schedule;

import org.apache.commons.lang.ArrayUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AspectLogger {

    private Logger logger = LoggerFactory.getLogger(AspectLogger.class);

    public Object log(ProceedingJoinPoint jp) throws Exception {
        Long before = System.currentTimeMillis();
        try {
            return jp.proceed();
        } catch (Throwable e) {
            throw new Exception(e);
        } finally {
            long time = System.currentTimeMillis() - before;
            logger.info(String.format("%s,%s,run about %s second,%s milliseconds", jp.getSignature().toString(), ArrayUtils.toString(jp.getArgs()), time / 1000, time));
        }
    }
}
