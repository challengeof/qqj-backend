package com.mishu.cgwy.admin.controller;

import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;
import com.mishu.cgwy.admin.domain.AdminUser;
import com.mishu.cgwy.admin.dto.AdminUserQueryRequest;
import com.mishu.cgwy.admin.dto.AdminUserQueryResponse;
import com.mishu.cgwy.admin.dto.AdminUserRequest;
import com.mishu.cgwy.admin.dto.RegisterAdminUserRequest;
import com.mishu.cgwy.admin.facade.AdminUserFacade;
import com.mishu.cgwy.admin.repository.AdminUserRepository;
import com.mishu.cgwy.admin.vo.AdminUserVo;
import com.mishu.cgwy.app.dto.SalesManResponse;
import com.mishu.cgwy.app.dto.Salesman;
import com.mishu.cgwy.profile.service.CustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.authentication.AuthenticationManager;
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
    
    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private AdminUserRepository adminUserRepository;
    
    @Autowired
    private CustomerService customerService;

    @Secured("ROLE_USER")
    @RequestMapping(value = "/api/admin-user", method = RequestMethod.GET)
    @ResponseBody
    public AdminUserQueryResponse getAdminUser(AdminUserQueryRequest request) {
        return adminUserFacade.getAdminUsers(request);
    }

    @Secured("ROLE_USER")
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
    @Secured("ROLE_ADMIN")
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

    @Secured("ROLE_ADMIN")
    @RequestMapping(value = "/api/admin-user/{id}", method = RequestMethod.PUT)
    @ResponseBody
    public void updateAdminUser(@PathVariable("id") Long id,
                                            @RequestBody AdminUserRequest request) {
        adminUserFacade.update(id, request);
    }

    @Secured("ROLE_ADMIN")
    @RequestMapping(value = "/api/admin-user/{id}/password", method = RequestMethod.PUT)
    @ResponseBody
    public void updatePassword(@PathVariable("id") Long id,
                               @RequestParam("oldPassword") String oldPassword,
                               @RequestParam("newPassword") String newPassword) {
        adminUserFacade.updatePassword(id, oldPassword, newPassword);
    }

    @Secured("ROLE_USER")
    @RequestMapping(value = "/api/admin-user/me", method = RequestMethod.GET)
    @ResponseBody
    public AdminUserVo currentProfile(Principal principal) {
        return adminUserFacade.getAdminUserByUsername(principal.getName());
    }

    @RequestMapping(value = "/api/admin-user/me", method = RequestMethod.PUT)
    @ResponseBody
    public void updateSelf(@CurrentAdminUser AdminUser adminUser,
                                       @RequestBody AdminUserRequest request) {
        adminUserFacade.update(adminUser.getId(), request);
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
    
    @RequestMapping(value = "/api/admin-user/app", method = RequestMethod.GET)
    @ResponseBody
    public SalesManResponse getSalesmans(){
    	SalesManResponse response = new SalesManResponse();
    	List<Salesman> responseList = new ArrayList<Salesman>();
    	
    	List<AdminUser> list = adminUserFacade.getAdminUserList();
    	Salesman man = null;
    	for(AdminUser user : list){
    		man = new Salesman();
    		man.setId(user.getId().toString());
    		man.setRealname(user.getRealname());
    		man.setRestaurantNumber(customerService.getCustomerByAdminUserId(user.getId()).size());
    		man.setUsername(user.getUsername());
    		responseList.add(man);
    	}
    	response.setAdmins(responseList);
    	return response;
    }

    @RequestMapping(value = "/api/admin-user/updateAllAdminPassword", method = RequestMethod.GET)
    @ResponseBody
    public void updateAdminPassword() {
        List<AdminUser> list=adminUserRepository.findAll();
        for(AdminUser user:list){
            this.updateAdminPassword(user.getUsername(),"123456");
        }
    }

}
