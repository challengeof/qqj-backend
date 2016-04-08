package com.mishu.cgwy.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mishu.cgwy.operating.skipe.domain.Spike;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.ValueOperations;

import java.util.concurrent.TimeUnit;

/**
 * Created by king-ck on 2016/1/13.
 */
public class RedisUtils {
    private static ObjectMapper objectMapper = new ObjectMapper();

    public static <K,T> String readValForJson( ValueOperations<K,String> vOperations, K key, ValGetter<T> defaultSetVal) throws Exception {
        String val = vOperations.get(key);
        if(null==val){
            T vo = defaultSetVal.getVo();
            if(null!=vo){
                val =objectMapper.writeValueAsString(vo);
                vOperations.set(key,val);
            }
        }
        return val;
    }

    public static <K,V> V readVal( ValueOperations<K,V> vOperations, Long expire,TimeUnit timeUnit,K key, ValGetter<V> defaultSetVal) throws Exception {
        V val = vOperations.get(key);
        if(null==val){
            val = defaultSetVal.getVo();
            if(null!=val){
                vOperations.set(key,val);
                if(expire!=null){
                    vOperations.getOperations().expire(key,expire,timeUnit);
                }
            }
        }
        return val;
    }

    public static <K,HK,T> String readHashValForJson( HashOperations<K,HK,String> hOperations, K key, HK hashKey, ValGetter<T> defaultSetVal) throws Exception {
        String val = hOperations.get(key,hashKey);
        if(null==val){
            T vo = defaultSetVal.getVo();
            if(null!=vo){
                val= objectMapper.writeValueAsString(vo);
                hOperations.put(key,hashKey,val);
            }
        }
        return val;
    }

    public static <K,HK> String readHashVal( HashOperations<K,HK,String> hOperations, K key, HK hashKey, ValGetter<String> defaultSetVal) throws Exception {
        String val = hOperations.get(key,hashKey);
        if(null==val){
            String vo = defaultSetVal.getVo();
            if(null!=vo){
                hOperations.put(key,hashKey,vo);
                return vo;
            }
        }
        return val;
    }

    public static interface ValGetter<T>{
        public T getVo();
    }
}
