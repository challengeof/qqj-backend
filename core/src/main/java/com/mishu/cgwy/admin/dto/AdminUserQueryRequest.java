package com.mishu.cgwy.admin.dto;

import lombok.Data;

/**
 * Created by xingdong on 15/7/30.
 */
@Data
public class AdminUserQueryRequest {
    private Long cityId;

    private Long depotId;

    private String username;

    private String realname;

    private String roleName;

    private String telephone;

    private Boolean isEnabled = true;

    private Boolean global;

    private Integer page = 0;

    private Integer pageSize = 100;

    private Long organizationId;
}
