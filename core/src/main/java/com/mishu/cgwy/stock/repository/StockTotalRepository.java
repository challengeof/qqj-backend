package com.mishu.cgwy.stock.repository;

import com.mishu.cgwy.stock.domain.Stock;
import com.mishu.cgwy.stock.domain.StockTotal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

/**
 * Created by xiao1zhao2 on 15/9/15.
 */
public interface StockTotalRepository extends JpaRepository<StockTotal, Long>, JpaSpecificationExecutor<StockTotal> {
    StockTotal findByCityIdAndSkuId(Long cityId, Long skuId);

    List<StockTotal> findBySkuId(Long skuId);
}
