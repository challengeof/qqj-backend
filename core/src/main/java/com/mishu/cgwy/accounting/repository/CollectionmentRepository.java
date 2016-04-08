package com.mishu.cgwy.accounting.repository;

import com.mishu.cgwy.accounting.domain.Collectionment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

/**
 * Created by admin on 10/12/15.
 */
public interface CollectionmentRepository extends JpaRepository<Collectionment, Long>, JpaSpecificationExecutor<Collectionment> {

}
