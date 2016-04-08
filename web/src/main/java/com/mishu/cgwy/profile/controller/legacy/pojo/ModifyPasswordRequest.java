package com.mishu.cgwy.profile.controller.legacy.pojo;

import lombok.Data;

@Data
public class ModifyPasswordRequest {
    private String oldPassword;
    private String newPassword;

}
