package com.mishu.cgwy.admin.repository;

import com.mishu.cgwy.admin.domain.AdminRole;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;


/**
 * User: xudong
 * Date: 12/2/14
 * Time: 3:26 PM
 */
public interface AdminRoleRepository extends JpaRepository<AdminRole, Long> {
    public List<AdminRole> findByName(String name);
}
