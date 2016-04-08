package com.mishu.cgwy.accounting.repository;

import com.mishu.cgwy.accounting.domain.VendorAccount;
import com.mishu.cgwy.accounting.domain.VendorAccountHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

/**
 * Created by wangguodong on 15/10/20.
 */
public interface VendorAccountHistoryRepository extends JpaRepository<VendorAccountHistory, Long>,JpaSpecificationExecutor<VendorAccountHistory> {
    List<VendorAccountHistory> findByPaymentId(Long paymentId);

    List<VendorAccountHistory> findByAccountPayableWriteoffId(Long id);
}
