package com.qqj.admin.facade;

import com.qqj.admin.domain.AdminPermission;
import com.qqj.admin.domain.AdminRole;
import com.qqj.admin.domain.AdminUser;
import com.qqj.admin.dto.AdminUserQueryRequest;
import com.qqj.admin.dto.AdminUserRequest;
import com.qqj.admin.dto.RegisterAdminUserRequest;
import com.qqj.admin.service.AdminUserService;
import com.qqj.admin.vo.AdminPermissionVo;
import com.qqj.admin.vo.AdminRoleVo;
import com.qqj.admin.vo.AdminUserVo;
import com.qqj.response.Response;
import com.qqj.response.query.QueryResponse;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * User: xudong
 * Date: 3/3/15
 * Time: 6:58 PM
 */
@Service
public class AdminUserFacade {

    @Autowired
    private AdminUserService adminUserService;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserDetailsService userDetailsService;

    @Transactional(readOnly = true)
    public UserDetails getUserDetailsByUsername(String username) {
        return userDetailsService.loadUserByUsername(username);
    }

    @Transactional(readOnly = true)
    public AdminUser getAdminUserEntityByUsername(String username) {
        return adminUserService.findAdminUserByUsername(username);
    }

    @Transactional(readOnly = true)
    public AdminUserVo getAdminUserByUsername(String username) {
        AdminUser adminUser = adminUserService.findAdminUserByUsername(username);
        AdminUserVo adminUserVo = new AdminUserVo();
        adminUserVo.setId(adminUser.getId());
        adminUserVo.setUsername(adminUser.getUsername());
        adminUserVo.setTelephone(adminUser.getTelephone());
        adminUserVo.setEnabled(adminUser.isEnabled());
        adminUserVo.setRealname(adminUser.getRealname());

        for (AdminRole role : adminUser.getAdminRoles()) {
            AdminRoleVo adminRoleVo = new AdminRoleVo();
            adminRoleVo.setId(role.getId());
            adminRoleVo.setName(role.getName());
            adminRoleVo.setDisplayName(role.getDisplayName());
            adminUserVo.getAdminRoles().add(adminRoleVo);
        }

        for (AdminRole role : adminUser.getAdminRoles()) {
            for (AdminPermission permission : role.getAdminPermissions()) {
                AdminPermissionVo adminPermissionVo = new AdminPermissionVo();
                adminPermissionVo.setName(permission.getName());
                adminPermissionVo.setId(permission.getId());
                adminPermissionVo.setDisplayName(permission.getDisplayName());
                adminUserVo.getAdminPermissions().add(adminPermissionVo);
            }
        }

        return adminUserVo;
    }

    @Transactional(readOnly = true)
    public List<AdminRoleVo> getAdminRoles(boolean showAdministrator) {
        List<AdminRoleVo> result = new ArrayList<>();
        for (AdminRole adminRole : adminUserService.getAdminRoles()) {
            if (!showAdministrator && adminRole.getName().equals("Administrator")) {
                continue;
            }
            AdminRoleVo adminRoleVo = new AdminRoleVo();
            adminRoleVo.setId(adminRole.getId());
            adminRoleVo.setName(adminRole.getName());
            adminRoleVo.setDisplayName(adminRole.getDisplayName());

            for (AdminPermission adminPermission : adminRole.getAdminPermissions()) {
                AdminPermissionVo adminPermissionVo = new AdminPermissionVo();
                adminPermissionVo.setId(adminPermission.getId());
                adminPermissionVo.setName(adminPermission.getName());
                adminPermissionVo.setDisplayName(adminPermission.getDisplayName());
                adminRoleVo.getAdminPermissions().add(adminPermissionVo);
            }

            result.add(adminRoleVo);
        }
        return result;
    }

    @Transactional(readOnly = true)
    public List<AdminPermissionVo> getAdminPermissions() {
        List<AdminPermissionVo> result = new ArrayList<>();
        for (AdminPermission adminPermission : adminUserService.findAllAdminPermissions()) {
            AdminPermissionVo vo = new AdminPermissionVo();
            vo.setId(adminPermission.getId());
            vo.setDisplayName(adminPermission.getDisplayName());
            vo.setName(adminPermission.getName());
            result.add(vo);
        }
        return result;
    }

    @Transactional
    public void updateAdminRolePermissions(Long roleId, List<Long> permissionIds) {
        final AdminRole role = adminUserService.getAdminRole(roleId);

        role.getAdminPermissions().clear();

        if(null != permissionIds && !permissionIds.isEmpty()){
        	List<AdminPermission> permissions = new ArrayList<>();
        	for (Long permissionId : permissionIds) {
        		permissions.add(adminUserService.getAdminPermission(permissionId));
        	}
        	role.getAdminPermissions().addAll(permissions);
        }

        adminUserService.saveAdminRole(role);
    }

    @Transactional
    public Response register(RegisterAdminUserRequest request) {
        AdminUser adminUser = new AdminUser();
        copyAttributes(request, adminUser);
        adminUser.setPassword(request.getPassword());

        return adminUserService.register(adminUser);
    }

    @Transactional
    public void update(Long id, AdminUserRequest request) {
        AdminUser adminUser = adminUserService.getAdminUser(id);
        copyAttributes(request, adminUser);
        adminUserService.update(adminUser);
    }

    private void copyAttributes(AdminUserRequest request, AdminUser adminUser) {
        adminUser.setUsername(StringUtils.trim(request.getUsername()));
        adminUser.setRealname(StringUtils.trim(request.getRealname()));
        adminUser.setTelephone(StringUtils.trim(request.getTelephone()));
        adminUser.setEnabled(request.isEnable());

        Set<AdminRole> roles = new HashSet<AdminRole>();
        for (Long roleId : request.getAdminRoleIds()) {
            roles.add(adminUserService.getAdminRole(roleId));
        }
        adminUser.setAdminRoles(roles);

    }

    @Transactional
    public void updatePassword(Long id, String oldPassword, String newPassword) {
        AdminUser adminUser = adminUserService.getAdminUser(id);
        UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(adminUser.getUsername(),
                adminUserService.getReformedPassword(adminUser.getUsername(), oldPassword));

        Authentication auth = authenticationManager.authenticate(token);

        if (auth.isAuthenticated()) {
            adminUserService.updateAdminUserPassword(adminUser, newPassword);
        }
    }

    @Transactional
    public boolean updatePassword(String username, String newPassword) {
        AdminUser adminUser = adminUserService.findAdminUserByUsername(username.trim());
        if (null != adminUser) {
            adminUserService.updateAdminUserPassword(adminUser, newPassword);
            return true;
        }
        return false;
    }

    @Transactional(readOnly = true)
    public Response<AdminUserVo> getAdminUsers(AdminUserQueryRequest request) {

        Page<AdminUser> page = adminUserService.getAdminUser(request);
        List<AdminUserVo> adminUsers = new ArrayList<>();
        for (AdminUser adminUser : page.getContent()) {
            AdminUserVo adminUserVo = new AdminUserVo();
            adminUserVo.setId(adminUser.getId());
            adminUserVo.setUsername(adminUser.getUsername());
            adminUserVo.setTelephone(adminUser.getTelephone());
            adminUserVo.setEnabled(adminUser.isEnabled());
            adminUserVo.setRealname(adminUser.getRealname());
            adminUsers.add(adminUserVo);
        }

        QueryResponse<AdminUserVo> response = new QueryResponse<AdminUserVo>();

        response.setContent(adminUsers);
        response.setTotal(page.getTotalElements());
        response.setPage(request.getPage());
        response.setPageSize(request.getPageSize());
        return response;
    }

    @Transactional(readOnly = true)
    public AdminUserVo getAdminUserById(Long id) {
        AdminUser adminUser = adminUserService.getAdminUser(id);
        AdminUserVo adminUserVo = new AdminUserVo();
        adminUserVo.setId(adminUser.getId());
        adminUserVo.setUsername(adminUser.getUsername());
        adminUserVo.setTelephone(adminUser.getTelephone());
        adminUserVo.setEnabled(adminUser.isEnabled());
        adminUserVo.setRealname(adminUser.getRealname());

        for (AdminRole role : adminUser.getAdminRoles()) {
            AdminRoleVo adminRoleVo = new AdminRoleVo();
            adminRoleVo.setId(role.getId());
            adminRoleVo.setName(role.getName());
            adminRoleVo.setDisplayName(role.getDisplayName());
            adminUserVo.getAdminRoles().add(adminRoleVo);
        }

        return adminUserVo;
    }

    @Transactional(readOnly = true)
    public AdminRoleVo getAdminRole(Long roleId) {
        AdminRole adminRole = adminUserService.getAdminRole(roleId);
        AdminRoleVo vo = new AdminRoleVo();
        vo.setId(adminRole.getId());
        vo.setName(adminRole.getName());
        vo.setDisplayName(adminRole.getDisplayName());

        for (AdminPermission adminPermission : adminRole.getAdminPermissions()) {
            AdminPermissionVo adminPermissionVo = new AdminPermissionVo();
            adminPermissionVo.setId(adminPermission.getId());
            adminPermissionVo.setName(adminPermission.getName());
            adminPermissionVo.setDisplayName(adminPermission.getDisplayName());
            vo.getAdminPermissions().add(adminPermissionVo);
        }

        return vo;
    }
}
