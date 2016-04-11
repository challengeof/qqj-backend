package com.mishu.cgwy.admin.controller;

import com.mishu.cgwy.admin.domain.AdminUser;
import com.mishu.cgwy.admin.facade.AdminUserFacade;
import com.mishu.cgwy.admin.vo.AdminPermissionVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * User: xudong
 * Date: 3/13/15
 * Time: 6:23 PM
 */
@Controller
public class AdminPermissionController {

    @Autowired
    private AdminUserFacade adminUserFacade;

    @Secured("ROLE_USER")
    @RequestMapping(value = "/api/admin-permission", method = RequestMethod.GET)
    @ResponseBody
    public List<AdminPermissionVo> adminRoles(@CurrentAdminUser AdminUser adminUser) {
        return adminUserFacade.getAdminPermissions();
    }
}
