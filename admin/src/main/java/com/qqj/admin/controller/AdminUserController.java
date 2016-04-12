package com.qqj.admin.controller;

import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;
import com.qqj.admin.domain.AdminUser;
import com.qqj.admin.dto.AdminUserQueryRequest;
import com.qqj.admin.dto.AdminUserQueryResponse;
import com.qqj.admin.dto.AdminUserRequest;
import com.qqj.admin.dto.RegisterAdminUserRequest;
import com.qqj.admin.facade.AdminUserFacade;
import com.qqj.admin.vo.AdminUserVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.ArrayList;
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
    public AdminUserQueryResponse getAdminUser(AdminUserQueryRequest request) {
        return adminUserFacade.getAdminUsers(request);
    }

    @RequestMapping(value = "/api/admin-user/global", method = RequestMethod.GET)
    @ResponseBody
    public List<AdminUserVo> listGlobalAdminUsers(AdminUserQueryRequest request) {
        return new ArrayList<>(Collections2.filter(adminUserFacade.getSimpleAdminUsers(request), new Predicate<AdminUserVo>() {
            @Override
            public boolean apply(AdminUserVo input) {
                return input.isGlobalAdmin();
            }
        }));
    }
    @RequestMapping(value = "/api/admin-user", method = RequestMethod.POST)
    @ResponseBody
    public void createAdminUser(@RequestBody RegisterAdminUserRequest request,@CurrentAdminUser AdminUser adminUser) {
        adminUserFacade.register(request,adminUser);
    }

    @RequestMapping(value = "/api/admin-user/{id}", method = RequestMethod.GET)
    @ResponseBody
    public AdminUserVo getAdminUser(@PathVariable("id") Long id) {
        return adminUserFacade.getAdminUserById(id);
    }

    @RequestMapping(value = "/api/admin-user/{id}", method = RequestMethod.PUT)
    @ResponseBody
    public void updateAdminUser(@PathVariable("id") Long id,
                                            @RequestBody AdminUserRequest request) {
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
}
