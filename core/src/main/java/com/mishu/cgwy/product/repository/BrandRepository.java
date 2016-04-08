package com.mishu.cgwy.product.repository;

import com.mishu.cgwy.product.domain.Brand;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

/**
 * User: xudong
 * Date: 3/5/15
 * Time: 6:59 PM
 */
public interface BrandRepository extends JpaRepository<Brand, Long>, JpaSpecificationExecutor<Brand> {
}
