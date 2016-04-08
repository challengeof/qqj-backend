package com.mishu.cgwy.admin.vo;

import com.mishu.cgwy.admin.domain.AdminPermission;
import com.mishu.cgwy.admin.domain.AdminRole;
import com.mishu.cgwy.admin.domain.AdminUser;
import com.mishu.cgwy.common.vo.BlockVo;
import com.mishu.cgwy.common.vo.CityVo;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
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

    private Long organizationId;

    private Set<AdminRoleVo> adminRoles = new HashSet<AdminRoleVo>();

    private Set<AdminPermissionVo> adminPermissions = new HashSet<AdminPermissionVo>();

    private Set<CityVo> cities = new HashSet<>();

    private Set<CityVo> depotCities = new HashSet<>();

    private String[] cityIds;

    private String[] depotCityIds;

    private String[] warehouseIds;

    private Set<BlockVo> blocks = new HashSet<>();

    private String[] blockIds;

    private String[] depotIds;
}
