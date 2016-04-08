package com.mishu.cgwy.stock.repository;

import com.mishu.cgwy.stock.domain.SellCancel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;


/**
 * User: admin
 * Date: 9/22/15
 * Time: 3:26 PM
 */
public interface SellCancelRepository extends JpaRepository<SellCancel, Long>, JpaSpecificationExecutor<SellCancel> {

    List<SellCancel> getSellCancelByOrderId(Long orderId);
}
