package com.mishu.cgwy.utils;

import org.apache.commons.beanutils.PropertyUtils;

import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by king-ck on 2016/3/4.
 */
public class BeanUtils {

    /**
     * 对两个对象的属性进行对比，筛选出差异
     */
    public static <T> Map<String,Object> propertyDiff(T oldO, T newO) throws IllegalAccessException, NoSuchMethodException, InvocationTargetException {

        Map<String, Object> params = new HashMap<>();
        PropertyDescriptor[] descriptors= PropertyUtils.getPropertyDescriptors(oldO);
        for(PropertyDescriptor pdesc : descriptors){
            Object oldVal =pdesc.getReadMethod().getDefaultValue();
            Object newVal = PropertyUtils.getProperty(newO,pdesc.getName());

            if(oldVal==newVal ){
                continue;
            }
            if(oldVal==null || !oldVal.equals(newVal)){
                params.put(pdesc.getName(),newVal);
            }
        }

        return params;
    }
}
