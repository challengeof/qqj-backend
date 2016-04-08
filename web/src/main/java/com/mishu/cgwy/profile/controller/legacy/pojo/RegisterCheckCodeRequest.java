package com.mishu.cgwy.profile.controller.legacy.pojo;

import lombok.Data;

/**
 * User: xudong
 * Date: 3/3/15
 * Time: 2:33 PM
 */
@Data
public class RegisterCheckCodeRequest {
    private String telephone;
    private Integer code;
    private Integer random;
}
