package com.mishu.cgwy.saleVisit.repository;

import com.mishu.cgwy.saleVisit.domain.SaleVisit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

/**
 * Created by apple on 15/8/13.
 */
public interface SaleVisitRepository extends JpaRepository<SaleVisit, Long>, JpaSpecificationExecutor<SaleVisit> {

}
