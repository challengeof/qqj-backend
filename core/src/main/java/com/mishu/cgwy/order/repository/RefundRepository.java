package com.mishu.cgwy.order.repository;

import com.mishu.cgwy.order.domain.Order;
import com.mishu.cgwy.order.domain.Refund;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

public interface RefundRepository extends JpaRepository<Refund, Long>,JpaSpecificationExecutor<Refund> {
    public List<Refund> findByOrder(Order order);

}
