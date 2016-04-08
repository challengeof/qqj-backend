package com.mishu.cgwy.purchase.repository;

import com.mishu.cgwy.purchase.domain.PurchaseOrderItemSign;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

/**
 * Created by wangguodong on 15/9/14.
 */
public interface PurchaseOrderItemSignRepository extends JpaRepository<PurchaseOrderItemSign,Long>,JpaSpecificationExecutor<PurchaseOrderItemSign> {
    public List<PurchaseOrderItemSign> findByCityIdAndDepotIdAndSkuId(Long cityId, Long depotId, Long skuId);
    public List<PurchaseOrderItemSign> findByCityIdAndDepotId(Long cityId, Long depotId);

}
