package com.mishu.cgwy.purchase.repository;

import com.mishu.cgwy.purchase.domain.ReturnNote;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

/**
 * Created by wangguodong on 15/10/10.
 */
public interface ReturnNoteRepository extends JpaRepository<ReturnNote, Long>, JpaSpecificationExecutor<ReturnNote> {

    List<ReturnNote> getReturnNoteByPurchaseOrderId(Long purchaseOrderId);
}
