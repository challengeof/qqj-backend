package com.mishu.cgwy.product.repository;

import com.mishu.cgwy.product.domain.SkuPriceHistory;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

public interface SkuPriceHistoryRepository extends JpaRepository<SkuPriceHistory, Long>, JpaSpecificationExecutor<SkuPriceHistory> {
    List<SkuPriceHistory> findByCityIdAndSkuIdAndTypeOrderByCreateDateDesc(Long cityId, Long skuId, Integer type, PageRequest pageRequest);
}
