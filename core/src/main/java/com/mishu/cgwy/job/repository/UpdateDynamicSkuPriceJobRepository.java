package com.mishu.cgwy.job.repository;

import com.mishu.cgwy.job.domain.UpdateDynamicSkuPriceJob;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

public interface UpdateDynamicSkuPriceJobRepository extends JpaRepository<UpdateDynamicSkuPriceJob, Long>, JpaSpecificationExecutor<UpdateDynamicSkuPriceJob> {
    List<UpdateDynamicSkuPriceJob> findBySkuIdAndWarehouseId(Long skuId, Long warehouseId);
}
