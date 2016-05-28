package com.qqj.org.repository;

import com.qqj.org.domain.PendingApprovalCustomer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface PendingApprovalCustomerRepository extends JpaRepository<PendingApprovalCustomer, Long>, JpaSpecificationExecutor<PendingApprovalCustomer> {
}
