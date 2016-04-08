package com.mishu.cgwy.admin.vo;

import com.mishu.cgwy.admin.domain.AdminPermission;
import lombok.Data;

@Data
public class AdminPermissionVo {
    private Long id;

    private String name;

    private String displayName;
}
