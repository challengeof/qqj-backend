package com.mishu.cgwy.accounting.repository;

import com.mishu.cgwy.accounting.domain.CollectionPaymentMethod;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

/**
 * Created by wangguodong on 15/10/12.
 */
public interface CollectionPaymentMethodRepository extends JpaRepository<CollectionPaymentMethod, Long>,JpaSpecificationExecutor<CollectionPaymentMethod> {
    public List<CollectionPaymentMethod> findByCityIdAndValid(Long cityId, boolean valid);
    public List<CollectionPaymentMethod> findByCityIdAndName(Long cityId, String name);
    public List<CollectionPaymentMethod> findByCityIdAndCode(Long cityId, String code);
    public List<CollectionPaymentMethod> findByCityIdAndCashTrueAndValidTrue(Long cityId);
}
