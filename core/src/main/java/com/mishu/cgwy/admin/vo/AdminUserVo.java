package com.mishu.cgwy.admin.vo;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.HashSet;
import java.util.Set;

/**
 * User: xudong
 * Date: 3/17/15
 * Time: 12:13 AM
 */
@Data
@EqualsAndHashCode(of = {"id"})
public class AdminUserVo {
    private Long id;

    private String username;

    private boolean enabled = true;

    private String telephone;

    private String realname;

    private boolean globalAdmin;

    private Set<AdminRoleVo> adminRoles = new HashSet<AdminRoleVo>();

    private Set<AdminPermissionVo> adminPermissions = new HashSet<AdminPermissionVo>();

}
