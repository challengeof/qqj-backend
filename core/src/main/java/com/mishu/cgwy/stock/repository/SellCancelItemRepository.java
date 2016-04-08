package com.mishu.cgwy.stock.repository;

import com.mishu.cgwy.stock.domain.SellCancelItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;


/**
 * User: admin
 * Date: 9/22/15
 * Time: 3:26 PM
 */
public interface SellCancelItemRepository extends JpaRepository<SellCancelItem, Long>, JpaSpecificationExecutor<SellCancelItem> {

}
