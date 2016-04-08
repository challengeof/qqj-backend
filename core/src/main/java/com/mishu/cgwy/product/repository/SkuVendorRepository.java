package com.mishu.cgwy.product.repository;

import com.mishu.cgwy.product.domain.SkuVendor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

public interface SkuVendorRepository extends JpaRepository<SkuVendor, Long>, JpaSpecificationExecutor<SkuVendor> {
    public List<SkuVendor> findByCityIdAndSkuId(Long cityId, Long skuId);

    public List<SkuVendor> findByCityIdAndVendorId(Long cityId, Long vendorId);
}
