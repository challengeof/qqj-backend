package com.mishu.cgwy.product.repository;

import com.mishu.cgwy.product.controller.SkuPriceListRequest;
import com.mishu.cgwy.product.domain.SkuPrice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface SkuPriceRepository extends JpaRepository<SkuPrice, Long>, JpaSpecificationExecutor<SkuPrice> {
    public List<SkuPrice> findByCityIdAndSkuId(Long cityId, Long skuId);
}
