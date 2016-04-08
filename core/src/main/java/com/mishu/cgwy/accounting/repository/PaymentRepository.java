package com.mishu.cgwy.accounting.repository;

import com.mishu.cgwy.accounting.domain.Payment;
import com.mishu.cgwy.purchase.domain.PurchaseOrder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Date;
import java.util.List;

/**
 * Created by wangguodong on 15/10/12.
 */
public interface PaymentRepository extends JpaRepository<Payment, Long>,JpaSpecificationExecutor<Payment> {
}
