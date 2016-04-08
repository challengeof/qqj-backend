package com.mishu.cgwy.application;

import com.mishu.cgwy.redis.JsonRedisSerializer;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by wangguodong on 16/1/28.
 */
@Service
public class Cache {

    private Map<String, Object> cache = new HashMap<>();

    public synchronized void set(String key, Object value) {
        cache.put(key, value);
    }

    public <T> T get(String key) {
        return (T) cache.get(key);
    }
}
