package com.mishu.cgwy.product.repository;

import com.mishu.cgwy.product.domain.Sku;
import com.mishu.cgwy.product.domain.SkuTag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

/**
 * User: xudong
 * Date: 3/5/15
 * Time: 6:59 PM
 */
public interface SkuTagRepository extends JpaRepository<SkuTag, Long>, JpaSpecificationExecutor<SkuTag> {
    public List<SkuTag> findBySkuIdAndCityId(Long skuId, Long cityId);

}
