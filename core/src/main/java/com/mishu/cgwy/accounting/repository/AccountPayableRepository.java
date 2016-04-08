package com.mishu.cgwy.accounting.repository;

import com.mishu.cgwy.accounting.domain.AccountPayable;
import com.mishu.cgwy.accounting.domain.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

/**
 * Created by wangguodong on 15/10/13.
 */
public interface AccountPayableRepository extends JpaRepository<AccountPayable, Long>,JpaSpecificationExecutor<AccountPayable> {
}
