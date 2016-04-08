package com.mishu.cgwy.salesPerformance.repository;

import com.mishu.cgwy.salesPerformance.domain.BlockSalesPerformance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Date;
import java.util.List;

/**
 * Created by xiao1zhao2 on 15/12/15.
 */
public interface BlockSalesPerformanceRepository extends JpaRepository<BlockSalesPerformance, Long>, JpaSpecificationExecutor<BlockSalesPerformance> {
    List<BlockSalesPerformance> getByBlockIdAndDate(Long blockId, Date date);
}
