package com.mishu.cgwy.inventory.repository;

import com.mishu.cgwy.inventory.domain.DynamicSkuPrice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

/**
 * User: xudong
 * Date: 3/5/15
 * Time: 6:59 PM
 */
public interface DynamicSkuPriceRepository extends JpaRepository<DynamicSkuPrice, Long>,
        JpaSpecificationExecutor<DynamicSkuPrice> {
    public List<DynamicSkuPrice> findBySkuId(Long skuId);

    public List<DynamicSkuPrice> findBySkuIdInAndWarehouseId(List<Long> skuIds, Long warehouseId);

    public List<DynamicSkuPrice> findByWarehouseId(Long warehouseId);
    
    public DynamicSkuPrice findBySkuIdAndWarehouseId(Long skuId, Long warehouseId);

    DynamicSkuPrice findBySkuIdAndWarehouseIdAndSkuStatus(Long id, Long warehosue, Integer value);
}
