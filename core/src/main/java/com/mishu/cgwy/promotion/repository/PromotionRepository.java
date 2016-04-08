package com.mishu.cgwy.promotion.repository;

import com.mishu.cgwy.promotion.domain.Promotion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface PromotionRepository extends JpaRepository<Promotion, Long>, JpaSpecificationExecutor<Promotion> {

    @Modifying
    @Query("update Promotion p set p.quantitySold = p.quantitySold + :number where p.id = :id and p.limitedQuantity - p.quantitySold > 0")
    int reduceLimited(@Param("id") Long id  , @Param("number") int number);

    @Modifying
    @Query("update Promotion p set p.end = now() where p.id = :id")
    int invalidatePromotion(@Param("id") Long id);
}
