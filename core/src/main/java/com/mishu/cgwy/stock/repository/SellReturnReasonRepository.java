package com.mishu.cgwy.stock.repository;

import com.mishu.cgwy.stock.domain.SellReturnReason;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;


/**
 * User: wangwei
 * Date: 10/12/15
 * Time: 3:26 PM
 */
public interface SellReturnReasonRepository extends JpaRepository<SellReturnReason, Long>, JpaSpecificationExecutor<SellReturnReason> {

}
