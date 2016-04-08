package com.mishu.cgwy.stock.repository;

import com.mishu.cgwy.stock.domain.AvgCostHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface AvgCostHistoryRepository extends JpaRepository<AvgCostHistory, Long>, JpaSpecificationExecutor<AvgCostHistory> {

}
