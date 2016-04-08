package com.mishu.cgwy.purchase.repository;

import com.mishu.cgwy.purchase.domain.ReturnNote;
import com.mishu.cgwy.purchase.domain.ReturnNoteItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

/**
 * Created by wangguodong on 15/10/12.
 */
public interface ReturnNoteItemRepository extends JpaRepository<ReturnNoteItem,Long>,JpaSpecificationExecutor<ReturnNoteItem> {
}
