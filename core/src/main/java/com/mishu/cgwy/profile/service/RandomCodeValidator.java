package com.mishu.cgwy.profile.service;

import com.mishu.cgwy.common.dto.RandomCode;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.RandomUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

/**
 * User: xudong
 * Date: 3/2/15
 * Time: 4:21 PM
 */
@Component
public class RandomCodeValidator {
    private static final Logger logger = LoggerFactory.getLogger(RandomCodeValidator.class);

    @Autowired(required = false)
    private StringRedisTemplate redisTemplate;

    public RandomCode generateRandom(String key) {
        String random = String.format("%04d", new java.util.Random().nextInt(10000));

        Integer code = RandomUtils.nextInt();

        RandomCode result = new RandomCode();
        result.setCode(code);
        result.setRandom(random);

        final String redisKey = key + ":" + result.getCode();
        redisTemplate.boundValueOps(redisKey).set(result.getRandom());
        redisTemplate.expire(redisKey, 30 * 60, TimeUnit.SECONDS);

        return result;
    }

    public boolean checkRandomCode(String key, Integer code, String random) {
        final String redisKey = key + ":" + code;

        final String value = redisTemplate.boundValueOps(redisKey).get();
        if (StringUtils.equals(random, value)) {
            // TODO: invalidate key if success
            // redisTemplate.delete(redisKey);
            return true;
        } else {
            return false;
        }
    }

}
