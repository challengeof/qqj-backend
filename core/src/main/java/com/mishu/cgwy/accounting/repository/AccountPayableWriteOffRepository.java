package com.mishu.cgwy.accounting.repository;

import com.mishu.cgwy.accounting.domain.AccountPayable;
import com.mishu.cgwy.accounting.domain.AccountPayableWriteoff;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

/**
 * Created by wangguodong on 15/10/14.
 */
public interface AccountPayableWriteOffRepository extends JpaRepository<AccountPayableWriteoff, Long>,JpaSpecificationExecutor<AccountPayableWriteoff> {
}
