package com.mishu.cgwy.job.service;

import com.mishu.cgwy.job.domain.UpdateDynamicSkuPriceJob;
import com.mishu.cgwy.job.repository.UpdateDynamicSkuPriceJobRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UpdateDynamicSkuPriceJobService {

    @Autowired
    private UpdateDynamicSkuPriceJobRepository updateDynamicSkuPriceRepository;

    public List<UpdateDynamicSkuPriceJob> findBySkuIdAndWarehouseId(Long skuId, Long warehouseId) {
        return updateDynamicSkuPriceRepository.findBySkuIdAndWarehouseId(skuId, warehouseId);
    }

    public void save(UpdateDynamicSkuPriceJob job) {
        updateDynamicSkuPriceRepository.save(job);
    }

    public void delete(Long id) {
        updateDynamicSkuPriceRepository.delete(id);
    }
}
