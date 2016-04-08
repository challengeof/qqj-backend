package com.mishu.cgwy.stock.repository;

import com.mishu.cgwy.stock.domain.StockOutItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;


/**
 * User: Admin
 * Date: 22/9/15
 * Time: 3:26 PM
 */
public interface StockOutItemRepository extends JpaRepository<StockOutItem, Long>, JpaSpecificationExecutor<StockOutItem> {

    List<StockOutItem> findBySkuId(Long skuId);
}
