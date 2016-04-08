package com.mishu.cgwy.common.repository;

import com.mishu.cgwy.common.domain.SystemProperties;
import com.mishu.cgwy.common.domain.Warehouse;
import org.springframework.data.jpa.repository.JpaRepository;


/**
 * User: xudong
 * Date: 12/2/14
 * Time: 3:26 PM
 */
public interface SystemPropertiesRepository extends JpaRepository<SystemProperties, Long> {
}
