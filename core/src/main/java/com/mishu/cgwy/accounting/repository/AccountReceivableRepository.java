package com.mishu.cgwy.accounting.repository;

import com.mishu.cgwy.accounting.domain.AccountReceivable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

/**
 * Created by xiao1zhao2 on 15/10/12.
 */
public interface AccountReceivableRepository extends JpaRepository<AccountReceivable, Long>, JpaSpecificationExecutor<AccountReceivable> {
}
