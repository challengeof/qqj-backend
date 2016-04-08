package com.mishu.cgwy.order.repository;

import com.mishu.cgwy.order.constants.OrderStatus;
import com.mishu.cgwy.order.domain.OrderItem;
import com.mishu.cgwy.product.domain.Sku;
import com.mishu.cgwy.product.domain.SkuSalesStatistics;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface OrderItemRepository extends JpaRepository<OrderItem, Long>, JpaSpecificationExecutor<OrderItem> {
    @Query("select oi.sku, sum(oi.countQuantity) as salesCount from OrderItem oi where oi.order.status = ?1 group by oi.sku")
    public List<Object[]> groupSaleCountBySku(int orderStatus);
}
