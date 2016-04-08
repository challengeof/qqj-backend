package com.mishu.cgwy.redis;

import com.mishu.cgwy.common.domain.City;
import org.hibernate.EmptyInterceptor;
import org.hibernate.type.Type;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.io.Serializable;
import java.lang.reflect.Field;

/**
 * Created by wangguodong on 16/1/27.
 */
@Service
public class RedisInterceptor extends EmptyInterceptor {

    @Autowired
    JsonRedisClient jsonRedisClient;

    @Override
    public void onDelete(
            Object entity,
            Serializable id,
            Object[] state,
            String[] propertyNames,
            Type[] types) {

        RedisCache redisCacheAnnotation = entity.getClass().getAnnotation(RedisCache.class);
        System.out.println("44444");
    }

    @Override
    public boolean onFlushDirty(
            Object entity,
            Serializable id,
            Object[] currentState,
            Object[] previousState,
            String[] propertyNames,
            Type[] types) {
        RedisCache redisCacheAnnotation = entity.getClass().getAnnotation(RedisCache.class);
        System.out.println(33333);
        return false;
    }

    @Override
    public boolean onLoad(
            Object entity,
            Serializable id,
            Object[] state,
            String[] propertyNames,
            Type[] types) {

        RedisCache redisCacheAnnotation = entity.getClass().getAnnotation(RedisCache.class);
        if (redisCacheAnnotation != null) {
            try {
                for (int i = 0; i < state.length; i++) {
                    String propertyName = propertyNames[i];
                    Object value = state[i];
                    Field field = entity.getClass().getDeclaredField(propertyName);
                    field.setAccessible(true);
                    field.set(entity, value);
                }
                String key = String.format("%s_%s", entity.getClass().getName(), id);
                jsonRedisClient.set(key, entity);
                return true;
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
        }
        return false;
    }

    @Override
    public Object getEntity(String entityName, Serializable id) {
        try {
            String key = String.format("%s_%s", entityName, id);
            Object entity = jsonRedisClient.get(key, Class.forName(entityName));

            if (entity != null) {
                return entity;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }
}
