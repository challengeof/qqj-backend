package com.qqj.purchase.repository;

import com.qqj.purchase.domain.Purchase;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface PurchaseRepository extends JpaRepository<Purchase, Long> , JpaSpecificationExecutor<Purchase>{
}
