package com.qqj.admin.vo;

import lombok.Data;

import java.util.HashSet;
import java.util.Set;

/**
 * User: xudong
 * Date: 3/3/15
 * Time: 10:52 AM
 */
@Data
public class AdminRoleVo {
    private Long id;

    private String name;

    private String displayName;

    private Set<AdminPermissionVo> adminPermissions = new HashSet<AdminPermissionVo>();
}
