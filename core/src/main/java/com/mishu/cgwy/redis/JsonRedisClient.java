package com.mishu.cgwy.redis;

import com.mishu.cgwy.common.domain.City;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * Created by wangguodong on 16/1/27.
 */
@Service
public class JsonRedisClient {

    @Resource
    private RedisTemplate redisTemplate;

    public void set(String key, Object value) {
        redisTemplate.setValueSerializer(new JsonRedisSerializer(Object.class));
        ValueOperations valueOperations = redisTemplate.opsForValue();
        System.out.println(String.format("redis set: %s %s", key, value));
        valueOperations.set(key, value);
    }

    public <T> T get(String key, Class<T> type) {
        redisTemplate.setValueSerializer(new JsonRedisSerializer<T>(type));
        ValueOperations<String, T> valueOperations = redisTemplate.opsForValue();
        T value = valueOperations.get(key);
        System.out.println(String.format("redis get: %s %s", key, value));
        return value;
    }


}
