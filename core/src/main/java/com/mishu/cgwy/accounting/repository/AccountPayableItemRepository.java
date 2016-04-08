package com.mishu.cgwy.accounting.repository;

import com.mishu.cgwy.accounting.domain.AccountPayable;
import com.mishu.cgwy.accounting.domain.AccountPayableItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

/**
 * Created by wangguodong on 15/10/21.
 */
public interface AccountPayableItemRepository extends JpaRepository<AccountPayableItem, Long>,JpaSpecificationExecutor<AccountPayableItem> {
}
