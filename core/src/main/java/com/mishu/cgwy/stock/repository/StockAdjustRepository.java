package com.mishu.cgwy.stock.repository;

import com.mishu.cgwy.stock.domain.StockAdjust;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface StockAdjustRepository extends JpaRepository<StockAdjust, Long>, JpaSpecificationExecutor<StockAdjust> {
}
