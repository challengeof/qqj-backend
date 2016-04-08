package com.mishu.cgwy.profile.controller.legacy.pojo;

import lombok.Data;

/**
 * User: xudong
 * Date: 3/3/15
 * Time: 3:30 PM
 */
@Data
public class LegacyRegisterRequest {
    private String telephone;
    private String password;
    private String recommendNumber;
    private Integer random;
    private Integer code;
    private boolean has;
    private Long regionId;
    private Long zoneId;
    private Long adminId;
}
