package com.mishu.cgwy.redis;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.SerializationException;
import org.springframework.data.redis.serializer.SerializationUtils;

import java.io.IOException;
import java.nio.charset.Charset;

/**
 * Created by wangguodong on 16/1/27.
 */
public class JsonRedisSerializer<T> implements RedisSerializer<T> {

    private Class<T> type = null;

    public JsonRedisSerializer(Class<T> type) {
        this.type = type;
    }
    static final byte[] EMPTY_ARRAY = new byte[0];

    private ObjectMapper objectMapper = new ObjectMapper();

    public byte[] serialize(Object object) {
        if (object== null) {
            return EMPTY_ARRAY;
        }
        try {
            return objectMapper.writeValueAsBytes(object);
        } catch (JsonProcessingException e) {
            throw new SerializationException("Could not write JSON: " + e.getMessage(), e);
        }
    }

    public T deserialize(byte[] bytes) {
        if(bytes == null || bytes.length == 0) {
            return null;
        }

        try {
            return objectMapper.readValue(bytes, 0, bytes.length, this.type);
        } catch (IOException e) {
            throw new SerializationException("Could not read JSON: " + e.getMessage(), e);
        }
    }


}