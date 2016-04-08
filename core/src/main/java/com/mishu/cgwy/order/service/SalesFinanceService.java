package com.mishu.cgwy.order.service;

import java.util.Date;
import java.util.List;

import com.mishu.cgwy.common.domain.Warehouse;
import com.mishu.cgwy.product.domain.Sku;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.mishu.cgwy.order.repository.SalesFinanceRepository;
import com.mishu.cgwy.order.domain.SalesFinance;

@Service
public class SalesFinanceService {
    @Autowired
    private SalesFinanceRepository salesFinanceRepository;
    
    public void save(SalesFinance salesFinance) {
    	salesFinanceRepository.save(salesFinance);
    }
    
    public List<SalesFinance> findByStatisticsDateAndWarehouseId(Date statisticsDate, Long warehouseId) {
    	return salesFinanceRepository.findByStatisticsDateAndWarehouseId(statisticsDate, warehouseId);
    }
    public SalesFinance findBySkuAndWarehouseAndStatisticsDate(Sku sku,Warehouse warehouse, Date statisticsDate) {
        return salesFinanceRepository.findBySkuAndWarehouseAndStatisticsDate(sku, warehouse, statisticsDate);
    }

}

