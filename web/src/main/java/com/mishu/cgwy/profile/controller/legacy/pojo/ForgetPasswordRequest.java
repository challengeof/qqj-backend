package com.mishu.cgwy.profile.controller.legacy.pojo;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class ForgetPasswordRequest extends RegisterCheckCodeRequest {
    private String password;

}
