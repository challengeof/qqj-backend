package com.mishu.cgwy.common.repository;

import com.mishu.cgwy.common.domain.SystemEmail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface SystemEmailRepository extends JpaRepository<SystemEmail, Long>, JpaSpecificationExecutor<SystemEmail> {
    SystemEmail findByCityIdAndType(Long cityId, int type);
}
