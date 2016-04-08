/*
 * #%L
 * BroadleafCommerce Profile
 * %%
 * Copyright (C) 2009 - 2013 Broadleaf Commerce
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *       http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */
package com.mishu.cgwy.admin.service;

import com.mishu.cgwy.admin.domain.AdminPermission;
import com.mishu.cgwy.admin.domain.AdminRole;
import com.mishu.cgwy.admin.domain.AdminUser;
import org.springframework.dao.DataAccessException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

public class AdminUserDetailsServiceImpl implements UserDetailsService {


    private AdminUserService adminUserService;

    @Override
    @Transactional
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException, DataAccessException {
        AdminUser customer = adminUserService.findAdminUserByUsername(username);
        if (customer == null) {
            throw new UsernameNotFoundException("The customer was not found");
        }

        List<GrantedAuthority> grantedAuthorities = new ArrayList<GrantedAuthority>();
        for (AdminRole role : customer.getAdminRoles()) {
            for (AdminPermission permission : role.getAdminPermissions()) {
                grantedAuthorities.add(new SimpleGrantedAuthority(permission.getName()));
            }
        }

        grantedAuthorities.add(new
                SimpleGrantedAuthority("ROLE_USER"));
        return new User(username, customer.getPassword(), customer.isEnabled(), true, true, true, grantedAuthorities);
    }

    public void setAdminUserService(AdminUserService adminUserService) {
        this.adminUserService = adminUserService;
    }
}