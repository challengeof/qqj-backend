package com.mishu.cgwy.common.repository;

import com.mishu.cgwy.common.domain.Version;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

/**
 * Created by kaicheng on 4/16/15.
 */
public interface VersionRepository extends JpaRepository<Version, Long>, JpaSpecificationExecutor<Version> {

    List<Version> findByVersionCodeGreaterThan(Integer versionCode);
}
