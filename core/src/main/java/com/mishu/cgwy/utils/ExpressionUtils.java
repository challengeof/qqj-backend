package com.mishu.cgwy.utils;

import com.mishu.cgwy.coupon.service.CouponService;
import com.mishu.cgwy.order.service.OrderService;
import org.mvel2.MVEL;
import org.mvel2.ParserContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.util.Map;

public class ExpressionUtils {
	
    private static Logger logger  = LoggerFactory.getLogger(CouponService.class);

    /**
     * Method used to determine whether the vars satisfy the expresstion or not.
     *
     * @param expression
     * @param vars
     * @return a Boolean object containing the result of executing the MVEL expression
     */
    public static Boolean executeExpression(String expression, Map<String, Object> vars) {
        logger.info(String.format("expression:%s", expression));
        logger.info(String.format("vars:%s", vars));

        try {
            Serializable exp;
            ParserContext context = new ParserContext();
            context.addImport("MVEL", MVEL.class);
            context.addImport("OrderService", OrderService.class);
            exp = MVEL.compileExpression(expression, context);
            Object test = MVEL.executeExpression(exp, vars);

            return (Boolean) test;

        } catch (Exception e) {
        	logger.error("Unable to parse and/or execute an mvel expression. Reporting to the logs and returning false " +
                    "for the match expression:" + expression, e);
            return false;
        }

    }
}
