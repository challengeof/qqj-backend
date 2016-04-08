package com.mishu.cgwy.stock.repository;

import com.mishu.cgwy.stock.domain.StockInItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;


/**
 * User: Admin
 * Date: 16/9/15
 * Time: 3:26 PM
 */
public interface StockInItemRepository extends JpaRepository<StockInItem, Long>, JpaSpecificationExecutor<StockInItem> {
   List <StockInItem> getStockInItemByStockInId(Long stockInId);

   List<StockInItem> findBySkuId(Long skuId);
}
