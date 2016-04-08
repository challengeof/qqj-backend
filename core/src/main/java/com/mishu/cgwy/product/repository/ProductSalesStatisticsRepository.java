package com.mishu.cgwy.product.repository;

import com.mishu.cgwy.product.domain.ProductSalesStatistics;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * User: xudong
 * Date: 3/5/15
 * Time: 6:59 PM
 */
public interface ProductSalesStatisticsRepository extends JpaRepository<ProductSalesStatistics, Long> {
}
