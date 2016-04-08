package com.mishu.cgwy.product.repository;

import com.mishu.cgwy.organization.domain.Organization;
import com.mishu.cgwy.product.domain.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

/**
 * User: xudong
 * Date: 3/5/15
 * Time: 6:59 PM
 */
public interface ProductRepository extends JpaRepository<Product, Long>, JpaSpecificationExecutor<Product> {
    List<Product> findByNameAndOrganization(String name, Organization organization);
}
