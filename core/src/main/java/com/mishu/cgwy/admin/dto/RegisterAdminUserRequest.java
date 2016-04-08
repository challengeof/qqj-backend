package com.mishu.cgwy.admin.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * User: xudong
 * Date: 3/13/15
 * Time: 6:28 PM
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class RegisterAdminUserRequest extends AdminUserRequest {
    private String password;
}
