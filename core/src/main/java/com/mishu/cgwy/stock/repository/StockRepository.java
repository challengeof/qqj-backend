package com.mishu.cgwy.stock.repository;

import com.mishu.cgwy.stock.domain.Stock;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;


/**
 * User: xudong
 * Date: 12/2/14
 * Time: 3:26 PM
 */
public interface StockRepository extends JpaRepository<Stock, Long>, JpaSpecificationExecutor<Stock> {
    List<Stock> findByStockOutId(Long stockOutItemId);
    List<Stock> findByShelfId(Long shelfId);
    List<Stock> findBySkuId(Long skuId);
    List<Stock> findByStockAdjustId(Long stockAdjustId);
}
