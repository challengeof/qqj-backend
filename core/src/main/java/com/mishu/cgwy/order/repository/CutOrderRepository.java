package com.mishu.cgwy.order.repository;

import com.mishu.cgwy.order.domain.CutOrder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface CutOrderRepository extends JpaRepository<CutOrder, Long>, JpaSpecificationExecutor<CutOrder> {
}
