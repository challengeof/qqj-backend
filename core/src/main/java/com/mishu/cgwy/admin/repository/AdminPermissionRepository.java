package com.mishu.cgwy.admin.repository;

import com.mishu.cgwy.admin.domain.AdminPermission;
import org.springframework.data.jpa.repository.JpaRepository;


/**
 * User: xudong
 * Date: 12/2/14
 * Time: 3:26 PM
 */
public interface AdminPermissionRepository extends JpaRepository<AdminPermission, Long> {
}
