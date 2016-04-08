package com.mishu.cgwy.order.repository;

import java.util.Date;
import java.util.List;

import com.mishu.cgwy.common.domain.Warehouse;
import com.mishu.cgwy.order.domain.SalesFinance;

import com.mishu.cgwy.product.domain.Sku;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface SalesFinanceRepository extends JpaRepository<SalesFinance, Long>, JpaSpecificationExecutor<SalesFinance> {
    public List<SalesFinance> findByStatisticsDateAndWarehouseId(Date statisticsDate, Long warehouseId);

    SalesFinance findBySkuAndWarehouseAndStatisticsDate(Sku sku, Warehouse warehouse, Date statisticsDate);
}
