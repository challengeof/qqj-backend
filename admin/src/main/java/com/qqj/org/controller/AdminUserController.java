package com.qqj.org.controller;

import com.qqj.admin.domain.AdminUser;
import com.qqj.admin.dto.AdminUserQueryRequest;
import com.qqj.admin.dto.AdminUserRequest;
import com.qqj.admin.dto.RegisterAdminUserRequest;
import com.qqj.admin.facade.AdminUserFacade;
import com.qqj.admin.vo.AdminPermissionVo;
import com.qqj.admin.vo.AdminRoleVo;
import com.qqj.admin.vo.AdminUserVo;
import com.qqj.response.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

/**
 * User: xudong
 * Date: 3/13/15
 * Time: 6:23 PM
 */
@Controller
public class AdminUserController {

    @Autowired
    private AdminUserFacade adminUserFacade;
    
    @RequestMapping(value = "/api/admin-user", method = RequestMethod.GET)
    @ResponseBody
    public Response<AdminUserVo> getAdminUser(AdminUserQueryRequest request) {
        return adminUserFacade.getAdminUsers(request);
    }

    @RequestMapping(value = "/api/admin-user", method = RequestMethod.POST)
    @ResponseBody
    public Response createAdminUser(@RequestBody RegisterAdminUserRequest request) {
        return adminUserFacade.register(request);
    }

    @RequestMapping(value = "/api/admin-user/{id}", method = RequestMethod.GET)
    @ResponseBody
    public AdminUserVo getAdminUser(@PathVariable("id") Long id) {
        return adminUserFacade.getAdminUserById(id);
    }

    @RequestMapping(value = "/api/admin-user/{id}", method = RequestMethod.PUT)
    @ResponseBody
    public void updateAdminUser(@PathVariable("id") Long id, @RequestBody AdminUserRequest request) {
        adminUserFacade.update(id, request);
    }

    @RequestMapping(value = "/api/admin-user/{id}/password", method = RequestMethod.PUT)
    @ResponseBody
    public void updatePassword(@PathVariable("id") Long id,
                               @RequestParam("oldPassword") String oldPassword,
                               @RequestParam("newPassword") String newPassword) {
        adminUserFacade.updatePassword(id, oldPassword, newPassword);
    }

    @RequestMapping(value = "/api/admin-user/me", method = RequestMethod.GET)
    @ResponseBody
    public AdminUserVo currentProfile(Principal principal) {
        return adminUserFacade.getAdminUserByUsername(principal.getName());
    }

    @RequestMapping(value = "/api/admin-user/me/password", method = RequestMethod.PUT)
    @ResponseBody
    public void updateSelfPassword(
            @CurrentAdminUser AdminUser currentAdminUser,
            @RequestParam("oldPassword") String oldPassword,
            @RequestParam("newPassword") String newPassword) {
        adminUserFacade.updatePassword(currentAdminUser.getId(), oldPassword, newPassword);
    }

    @RequestMapping(value = "/api/admin-user/updateAdminPassword", method = RequestMethod.POST)
    @ResponseBody
    public boolean updateAdminPassword(@RequestParam("username") String username,@RequestParam("password") String password) {
        return adminUserFacade.updatePassword(username, password);
    }

    @RequestMapping(value = "/api/admin-permission", method = RequestMethod.GET)
    @ResponseBody
    public List<AdminPermissionVo> adminPermissions(@CurrentAdminUser AdminUser adminUser) {
        return adminUserFacade.getAdminPermissions();
    }

    @RequestMapping(value = "/api/admin-role", method = RequestMethod.GET)
    @ResponseBody
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
