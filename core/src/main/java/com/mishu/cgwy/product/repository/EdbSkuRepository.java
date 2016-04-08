package com.mishu.cgwy.product.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.mishu.cgwy.product.domain.EdbSku;

import java.util.Date;
import java.util.List;

public interface EdbSkuRepository extends JpaRepository<EdbSku, Long>, JpaSpecificationExecutor<EdbSku> {
    public List<EdbSku> findByStockDate(Date date);
    public EdbSku findBySkuIdAndStockDate(Long skuId, Date stockDate);
}
