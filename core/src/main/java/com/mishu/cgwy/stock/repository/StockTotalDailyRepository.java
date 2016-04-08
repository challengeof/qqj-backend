package com.mishu.cgwy.stock.repository;

import com.mishu.cgwy.stock.domain.StockTotalDaily;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface StockTotalDailyRepository extends JpaRepository<StockTotalDaily, Long>, JpaSpecificationExecutor<StockTotalDaily> {

}
