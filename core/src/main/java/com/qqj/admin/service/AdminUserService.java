package com.qqj.admin.service;

import com.google.common.collect.Collections2;
import com.qqj.admin.domain.*;
import com.qqj.admin.dto.AdminUserQueryRequest;
import com.qqj.admin.repository.AdminPermissionRepository;
import com.qqj.admin.repository.AdminRoleRepository;
import com.qqj.admin.repository.AdminUserRepository;
import com.qqj.error.AdminUserAlreadyExistsException;
import com.qqj.response.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.criteria.*;
import java.util.ArrayList;
import java.util.List;

/**
 * User: xudong
 * Date: 2/28/15
 * Time: 6:02 PM
 */
@Service
public class AdminUserService {
    @Autowired
    private AdminUserRepository adminUserRepository;

    @Autowired
    private AdminRoleRepository adminRoleRepository;

    @Autowired
    private AdminPermissionRepository adminPermissionRepository;

    @Autowired(required = false)
    private PasswordEncoder passwordEncoder;

    @Transactional
    public Response register(AdminUser adminUser) {
        if (findAdminUserByUsername(adminUser.getUsername()) != null) {
            Response<AdminUser> res = new Response<>();
            res.setSuccess(Boolean.FALSE);
            res.setMsg("用户已存在");
            return res;
        }
        adminUser.setPassword(passwordEncoder.encode(getReformedPassword(adminUser.getUsername(), adminUser.getPassword())));

        adminUserRepository.save(adminUser);

        return Response.successResponse;
    }

    @Transactional
    public AdminUser update(AdminUser adminUser) {
        final AdminUser adminUserByUsername = findAdminUserByUsername(adminUser.getUsername());
        if (adminUserByUsername != null && !adminUserByUsername.getId().equals(adminUser.getId())) {
            throw new AdminUserAlreadyExistsException();
        }

        return adminUserRepository.save(adminUser);
    }

    @Transactional
    public AdminUser updateAdminUserPassword(AdminUser adminUser, String password) {
        adminUser.setPassword(passwordEncoder.encode(getReformedPassword(adminUser.getUsername(), password)));

        return adminUserRepository.save(adminUser);
    }

    /**
     * 兼容原有系统密码规则
     *
     * @param username
     * @param password
     * @return
     */
    public String getReformedPassword(String username, String password) {
        return username + password + "mirror";
    }

    @Transactional(readOnly = true)
    public AdminUser findAdminUserByUsername(String username) {
        final List<AdminUser> list = adminUserRepository.findByUsername(username);
        if (list.isEmpty()) {
            return null;
        } else {
            return list.get(0);
        }
    }

    @Transactional(readOnly = true)
    public AdminRole getAdminRole(Long roleId) {
        return adminRoleRepository.getOne(roleId);
    }

    @Transactional(readOnly = true)
    public AdminRole getAdminRole(String name) {
        List<AdminRole> list = adminRoleRepository.findByName(name);
        if (null != list && !list.isEmpty()) {
            return list.get(0);
        }
        return null;
    }


    @Transactional
    public AdminRole saveAdminRole(AdminRole adminRole) {
        return adminRoleRepository.save(adminRole);
    }

    @Transactional(readOnly = true)
    public List<AdminRole> getAdminRoles() {
        return adminRoleRepository.findAll();
    }

    @Transactional
    public AdminPermission saveAdminPermission(AdminPermission adminPermission) {
        return adminPermissionRepository.save(adminPermission);
    }

    @Transactional(readOnly = true)
    public List<AdminPermission> findAllAdminPermissions() {
        return adminPermissionRepository.findAll();
    }

    @Transactional
    public AdminPermission getAdminPermission(Long id) {
        return adminPermissionRepository.getOne(id);
    }

    @Transactional(readOnly = true)
    public AdminUser getAdminUser(Long id) {
        return adminUserRepository.getOne(id);
    }

    @Transactional(readOnly = true)
    public List<AdminUser> getAllAdminUsers() {
        return new ArrayList<>(Collections2.filter(adminUserRepository.findAll(), new com.google.common.base.Predicate<AdminUser>
                () {
            @Override
            public boolean apply(AdminUser input) {
                return input.isEnabled();
            }
        }));
    }

    @Transactional(readOnly = true)
    public Page<AdminUser> getAdminUser(final AdminUserQueryRequest request) {

        final Pageable pageable = new PageRequest(request.getPage(), request.getPageSize());

        return adminUserRepository.findAll(new Specification<AdminUser>() {
            @Override
            public Predicate toPredicate(Root<AdminUser> root, CriteriaQuery<?> query, CriteriaBuilder cb) {

                List<Predicate> predicates = new ArrayList<Predicate>();

                if (request.getEnabled() != null) {
                    predicates.add(cb.equal(root.get(AdminUser_.enabled), request.getEnabled()));
                }

                if (request.getRealname() != null) {
                    predicates.add(cb.like(root.get(AdminUser_.realname), "%" + request.getRealname() + "%"));
                }

                if (request.getUsername() != null) {
                    predicates.add(cb.like(root.get(AdminUser_.username), "%" + request.getUsername() + "%"));
                }

                if (request.getTelephone() != null) {
                    predicates.add(cb.like(root.get(AdminUser_.telephone), "%" + request.getTelephone() + "%"));
                }

                return cb.and(predicates.toArray(new Predicate[predicates.size()]));
            }
        }, pageable);
    }


    @Transactional(readOnly = true)
    public List<AdminUser> getAdminUsersByRole(AdminRole role) {
        return new ArrayList<>(Collections2.filter(adminUserRepository.findAdminUserByAdminRole(role), new com.google.common.base.Predicate<AdminUser>() {
            @Override
            public boolean apply(AdminUser input) {
                return input.isEnabled();
            }
        }));
    }

    public AdminUser findOne(Long id) {
        return adminUserRepository.findOne(id);
    }

    public AdminUser getOne(Long id) {
        return adminUserRepository.getOne(id);
    }

}
