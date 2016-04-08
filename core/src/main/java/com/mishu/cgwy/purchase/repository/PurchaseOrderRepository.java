package com.mishu.cgwy.purchase.repository;

import com.mishu.cgwy.profile.domain.Complaint;
import com.mishu.cgwy.purchase.domain.PurchaseOrder;
import com.mishu.cgwy.purchase.domain.PurchaseOrderItem;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Date;
import java.util.List;

/**
 * Created by wangguodong on 15/9/14.
 */
public interface PurchaseOrderRepository extends JpaRepository<PurchaseOrder,Long>,JpaSpecificationExecutor<PurchaseOrder> {

    public List<PurchaseOrder> findByVendorIdAndExpectedArrivedDate(Long vendorId, Date expectedArrivedDate);
}
