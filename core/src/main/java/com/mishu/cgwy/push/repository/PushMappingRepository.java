package com.mishu.cgwy.push.repository;

import com.mishu.cgwy.push.domain.PushMapping;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

public interface PushMappingRepository extends JpaRepository<PushMapping, Long>, JpaSpecificationExecutor<PushMapping> {

    List<PushMapping> findByBaiduChannelId(String baiduChannelId);

    List<PushMapping> findByCustomerId(Long customerId);
}
