package com.mishu.cgwy.stock.repository;

import com.mishu.cgwy.stock.domain.StockOut;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

/**
 * Created by xiao1zhao2 on 15/9/16.
 */
public interface StockOutRepository extends JpaRepository<StockOut, Long>, JpaSpecificationExecutor<StockOut> {

    StockOut getStockOutByOrderId(Long orderId);
}
