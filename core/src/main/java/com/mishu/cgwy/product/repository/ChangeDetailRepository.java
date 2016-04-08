package com.mishu.cgwy.product.repository;

import com.mishu.cgwy.product.domain.ChangeDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

/**
 * Created by bowen on 15-6-4.
 */
public interface ChangeDetailRepository extends JpaRepository<ChangeDetail, Long>, JpaSpecificationExecutor<ChangeDetail> {
}
