package com.mishu.cgwy.product.repository;

import com.mishu.cgwy.product.domain.Sku;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Collection;
import java.util.List;

/**
 * User: xudong
 * Date: 3/5/15
 * Time: 6:59 PM
 */
public interface SkuRepository extends JpaRepository<Sku, Long>, JpaSpecificationExecutor<Sku> {
    public List<Sku> findByProductId(Long productId);

    public List<Sku> findByIdIn(Collection<Long> skuIds);

}
