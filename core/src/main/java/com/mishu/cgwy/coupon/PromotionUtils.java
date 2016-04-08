package com.mishu.cgwy.coupon;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mishu.cgwy.coupon.constant.PromotionRuleConstant;
import com.mishu.cgwy.coupon.controller.PromotionRequest;
import org.apache.commons.lang.StringUtils;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by wangguodong on 16/3/30.
 */
public class PromotionUtils {
    public static String getRealRule(String rule, String ruleValue) throws RuntimeException {
        String realValue = rule;
        try {
            PromotionRequest request = new ObjectMapper().readValue(ruleValue, PromotionRequest.class);
            Field[] fields = PromotionRequest.class.getDeclaredFields();
            for (Field field : fields) {
                field.setAccessible(true);
                Object valueObject = field.get(request);
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

                realValue = realValue.replaceAll("\\" + PromotionRuleConstant.PREFIX + field.getName(), value);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return realValue;
    }
}
