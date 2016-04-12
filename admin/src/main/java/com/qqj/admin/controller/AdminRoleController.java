package com.qqj.admin.controller;

import com.qqj.admin.domain.AdminUser;
import com.qqj.admin.facade.AdminUserFacade;
import com.qqj.admin.vo.AdminRoleVo;
import com.qqj.utils.PermissionCheckUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

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
