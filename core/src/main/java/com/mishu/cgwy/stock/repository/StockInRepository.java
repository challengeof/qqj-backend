package com.mishu.cgwy.stock.repository;

import com.mishu.cgwy.stock.domain.StockIn;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;


/**
 * User: Admin
 * Date: 16/9/15
 * Time: 3:26 PM
 */
public interface StockInRepository extends JpaRepository<StockIn, Long>, JpaSpecificationExecutor<StockIn> {
    List<StockIn> findByPurchaseOrderId(Long purchaseOrderId);
}
