package com.mishu.cgwy.common.repository;

import com.mishu.cgwy.common.domain.Region;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;


/**
 * User: xudong
 * Date: 12/2/14
 * Time: 3:26 PM
 */
public interface RegionRepository extends JpaRepository<Region, Long> {

    List<Region> findByCityId(Long cityId);
}
