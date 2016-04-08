package com.mishu.cgwy.inventory.repository;

import com.mishu.cgwy.inventory.domain.Vendor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

/**
 * User: xudong
 * Date: 3/5/15
 * Time: 6:59 PM
 */
public interface VendorRepository extends JpaRepository<Vendor, Long>, JpaSpecificationExecutor<Vendor> {

    List<Vendor> findByUsername(String username);

    List<Vendor> findByName(String name);

    List<Vendor> findByNameAndOrganizationId(String name, Long organizationId);
}
