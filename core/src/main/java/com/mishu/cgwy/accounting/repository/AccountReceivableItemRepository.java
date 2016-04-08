package com.mishu.cgwy.accounting.repository;

import com.mishu.cgwy.accounting.domain.AccountReceivableItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

/**
 * Created by xiao1zhao2 on 15/10/12.
 */
public interface AccountReceivableItemRepository extends JpaRepository<AccountReceivableItem, Long>, JpaSpecificationExecutor<AccountReceivableItem> {
}
