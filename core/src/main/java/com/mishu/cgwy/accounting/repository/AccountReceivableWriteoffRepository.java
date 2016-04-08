package com.mishu.cgwy.accounting.repository;

import com.mishu.cgwy.accounting.domain.AccountReceivableWriteoff;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

/**
 * Created by admin on 15/10/13.
 */
public interface AccountReceivableWriteoffRepository extends JpaRepository<AccountReceivableWriteoff, Long>,JpaSpecificationExecutor<AccountReceivableWriteoff> {
}
