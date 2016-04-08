package com.mishu.cgwy.vendor.repository;

import com.mishu.cgwy.stock.domain.AvgCostHistory;
import com.mishu.cgwy.vendor.domain.VendorOrderItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

public interface VendorOrderItemRepository extends JpaRepository<VendorOrderItem, Long>, JpaSpecificationExecutor<VendorOrderItem> {
    public List<VendorOrderItem> findBySkuIdAndVendorIdAndDepotId(Long skuId, Long vendorId, Long depotId);
}
