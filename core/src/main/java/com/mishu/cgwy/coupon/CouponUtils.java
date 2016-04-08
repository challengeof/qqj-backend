package com.mishu.cgwy.coupon;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mishu.cgwy.coupon.constant.CouponRuleConstant;
import com.mishu.cgwy.coupon.controller.CouponRequest;
import org.apache.commons.lang.StringUtils;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by wangguodong on 16/3/30.
 */
public class CouponUtils {
    public static String getRealRule(String rule, String ruleValue) throws RuntimeException {
        String realValue = rule;
        try {
            CouponRequest couponRequest = new ObjectMapper().readValue(ruleValue, CouponRequest.class);
            Field[] fields = CouponRequest.class.getDeclaredFields();
            for (Field field : fields) {
                field.setAccessible(true);
                Object valueObject = field.get(couponRequest);
                String value = StringUtils.EMPTY;
                if (valueObject != null) {
                    if (valueObject.getClass().isArray()) {
                        List<Object> objectList = new ArrayList<>();
                        for (int i = 0; i < Array.getLength(valueObject); i++) {
                            objectList.add(Array.get(valueObject, i));
                        }
                        value = StringUtils.join(objectList, ",");
                    } else {
                        value = valueObject.toString();
                    }
                }

                realValue = realValue.replaceAll("\\" + CouponRuleConstant.PREFIX + field.getName(), value);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return realValue;
    }
}
