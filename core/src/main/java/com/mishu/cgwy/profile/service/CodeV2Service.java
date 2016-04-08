package com.mishu.cgwy.profile.service;

import com.mishu.cgwy.error.TooMuchCodeRetryException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.BoundHashOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

/**
 * User: xudong
 * Date: 3/2/15
 * Time: 7:40 PM
 */
@Service
public class CodeV2Service {

    @Autowired(required = false)
    private StringRedisTemplate redisTemplate;

    @Autowired
    private ISmsProvider smsProvider;

    private String registerCodeTemplate = "【餐馆无忧】您的验证码是%s，请在页面中输入以完成验证。如有问题请致电客服。";

    public boolean checkCode(String telephone, String code) {
        if (redisTemplate.hasKey(telephone)) {
            final BoundHashOperations<String, String, Object> ops = redisTemplate
                    .boundHashOps(telephone);

            String expected = ops.get("code").toString();
            if (expected.equals(code)) {
                return true;
            } else {
                ops.increment("count", 1);
                if (Long.valueOf(ops.get("count").toString()) > 10) {
                    throw new TooMuchCodeRetryException();
                }
                return false;
            }

        } else {
            return false;
        }
    }


    public String sendCode(String telephone) {
        redisTemplate.delete(telephone);
        String code = String.format("%04d", new java.util.Random().nextInt(10000));
        final BoundHashOperations<String, String, Object> ops = redisTemplate
                .boundHashOps(telephone);
        ops.put("code", code);
        redisTemplate.expire(telephone, 30 * 60, TimeUnit.SECONDS);

        smsProvider.send(String.format(registerCodeTemplate, code), telephone);
        return code;
    }

    public void setRegisterCodeTemplate(String registerCodeTemplate) {
        this.registerCodeTemplate = registerCodeTemplate;
    }

}
