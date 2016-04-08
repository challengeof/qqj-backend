package com.mishu.cgwy.admin.controller;

import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;
import com.mishu.cgwy.admin.domain.AdminUser;
import com.mishu.cgwy.admin.facade.AdminUserFacade;
import com.mishu.cgwy.admin.vo.AdminRoleVo;
import com.mishu.cgwy.order.facade.PermissionCheckUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

/**
 * User: xudong
 * Date: 3/13/15
 * Time: 6:23 PM
 */
@Controller
public class AdminRoleController {

    @Autowired
    private AdminUserFacade adminUserFacade;

    @RequestMapping(value = "/api/admin-role", method = RequestMethod.GET)
    @ResponseBody
    public List<AdminRoleVo> adminRoles(@CurrentAdminUser AdminUser adminUser) {
        return PermissionCheckUtils.filterAccessibleAdminRole(adminUserFacade.getAdminRoles(), adminUser);
    }


    @RequestMapping(value = "/api/admin-roles/organization", method = RequestMethod.GET)
    @ResponseBody
    public List<AdminRoleVo> adminUserHas(@CurrentAdminUser AdminUser adminUser) {
        List<AdminRoleVo> adminRoleWrappers = new ArrayList<>(Collections2.filter(adminUserFacade.getAdminRoles(), new Predicate<AdminRoleVo>() {
            @Override
            public boolean apply(AdminRoleVo input) {
                return input.isOrganizationRole();
            }
        }));
        if(!adminUser.isGlobalAdmin()) {
            adminRoleWrappers = PermissionCheckUtils.filterAccessibleAdminRole(adminRoleWrappers,adminUser);
        }
        return  adminRoleWrappers;
    }

    @RequestMapping(value = "/api/admin-role/{id}", method = RequestMethod.PUT)
    @ResponseBody
    public void updateAdminRolePermissions(@PathVariable("id") Long roleId,
                                                       @RequestParam(value="permissions[]", required=false) List<Long> permissions,
                                                       @CurrentAdminUser AdminUser adminUser) {
        adminUserFacade.updateAdminRolePermissions(roleId, permissions);
    }

    @RequestMapping(value = "/api/admin-role/{id}", method = RequestMethod.GET)
    @ResponseBody
    public AdminRoleVo getAdminRole(@PathVariable("id") Long roleId,
                                                       @CurrentAdminUser AdminUser adminUser) {
        return adminUserFacade.getAdminRole(roleId);
    }


}
