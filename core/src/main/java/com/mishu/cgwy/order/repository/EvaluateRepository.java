package com.mishu.cgwy.order.repository;

import com.mishu.cgwy.order.domain.Evaluate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;



public interface EvaluateRepository extends JpaRepository<Evaluate, Long>,JpaSpecificationExecutor<Evaluate>{


    Evaluate findEvaluateByOrderId(Long orderId);
}
