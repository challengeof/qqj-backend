package com.mishu.cgwy.admin.repository;

import com.mishu.cgwy.admin.domain.AdminRole;
import com.mishu.cgwy.admin.domain.AdminUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.util.List;


/**
 * User: xudong
 * Date: 12/2/14
 * Time: 3:26 PM
 */
public interface AdminUserRepository extends JpaRepository<AdminUser, Long> , JpaSpecificationExecutor<AdminUser>{
    List<AdminUser> findByUsername(String username);

    @Query(value = "SELECT au FROM AdminUser au JOIN au.adminRoles r WHERE r = ?1")
    public List<AdminUser> findAdminUserByAdminRole(AdminRole role);

}
