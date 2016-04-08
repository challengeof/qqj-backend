package com.mishu.cgwy.common.repository;

import com.mishu.cgwy.common.domain.Zone;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;


/**
 * User: xudong
 * Date: 12/2/14
 * Time: 3:26 PM
 */
public interface ZoneRepository extends JpaRepository<Zone, Long> {
    List<Zone> findByRegionId(Long regionId);

    List<Zone> findByCityId(Long cityId);
}
