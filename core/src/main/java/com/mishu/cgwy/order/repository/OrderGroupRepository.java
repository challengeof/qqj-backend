package com.mishu.cgwy.order.repository;

import com.mishu.cgwy.order.domain.Order;
import com.mishu.cgwy.order.domain.OrderGroup;
import org.jboss.logging.annotations.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface OrderGroupRepository extends JpaRepository<OrderGroup, Long>, JpaSpecificationExecutor<OrderGroup> {

    @Query(value = "SELECT og FROM OrderGroup og JOIN og.members m WHERE m = ?1")
    public List<OrderGroup> findOrderGroupByOrder(Order order);

}
