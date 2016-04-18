package com.qqj.org.controller;

import com.qqj.admin.domain.AdminUser;
import com.qqj.admin.facade.AdminUserFacade;
import com.qqj.admin.vo.AdminPermissionVo;
import com.qqj.admin.vo.AdminRoleVo;
import com.qqj.org.facade.OrgFacade;
import com.qqj.org.vo.TeamVo;
import com.qqj.response.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Created by wangguodong on 16/4/12.
 */
@Controller
public class OrgController {

    @Autowired
    private AdminUserFacade adminUserFacade;

    @Autowired
    private OrgFacade orgFacade;

    @RequestMapping(value = "/org/team/list", method = RequestMethod.GET)
    @ResponseBody
    public Response<TeamVo> adminRoles(TeamListRequest request) {
        return orgFacade.getTeamList(request);
    }

    @Secured("ROLE_USER")
    @RequestMapping(value = "/api/admin-permission", method = RequestMethod.GET)
    @ResponseBody
    public List<AdminPermissionVo> adminPermissions(@CurrentAdminUser AdminUser adminUser) {
        return adminUserFacade.getAdminPermissions();
    }

    public List<AdminRoleVo> adminRoles(@CurrentAdminUser AdminUser adminUser) {
        return adminUserFacade.getAdminRoles();
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
