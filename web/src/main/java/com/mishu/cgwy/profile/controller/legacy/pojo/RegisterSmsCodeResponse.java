package com.mishu.cgwy.profile.controller.legacy.pojo;

import com.mishu.cgwy.error.RestError;

/**
 * User: xudong
 * Date: 3/2/15
 * Time: 8:05 PM
 */
public class RegisterSmsCodeResponse extends RestError {
    private Integer code;

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

}
