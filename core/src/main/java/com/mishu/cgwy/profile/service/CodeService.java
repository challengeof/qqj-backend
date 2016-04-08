package com.mishu.cgwy.profile.service;

import com.mishu.cgwy.common.dto.RandomCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * User: xudong
 * Date: 3/2/15
 * Time: 7:40 PM
 */
@Service
public class CodeService {
    @Autowired
    private RandomCodeValidator randomCodeValidator;

    @Autowired
    private ISmsProvider smsProvider;

    private String registerCodeTemplate = "【餐馆无忧】您的验证码是%s，请在页面中输入以完成验证。如有问题请致电客服。";

    public Integer sendRandomForRegister(String telephone) {
        final RandomCode randomCode = randomCodeValidator.generateRandom(telephone);
        smsProvider.send(String.format(registerCodeTemplate, randomCode.getRandom()), telephone);
        return randomCode.getCode();
    }

    public boolean checkCodeForRegister(String telephone, Integer code, String random) {
        return randomCodeValidator.checkRandomCode(telephone, code, random);
    }

    public void setRegisterCodeTemplate(String registerCodeTemplate) {
        this.registerCodeTemplate = registerCodeTemplate;
    }
}
