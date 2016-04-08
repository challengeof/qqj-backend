package com.mishu.cgwy.purchase.repository;

import com.mishu.cgwy.purchase.domain.PurchaseOrderItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

/**
 * Created by wangguodong on 15/9/14.
 */
public interface PurchaseOrderItemRepository extends JpaRepository<PurchaseOrderItem,Long>,JpaSpecificationExecutor<PurchaseOrderItem> {
    List<PurchaseOrderItem> findBySkuId(Long id);
}
