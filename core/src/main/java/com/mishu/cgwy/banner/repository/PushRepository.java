package com.mishu.cgwy.banner.repository;

import com.mishu.cgwy.banner.domain.Push;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

/**
 * Created by bowen on 15-7-29.
 */
public interface PushRepository extends JpaRepository<Push,Long> ,JpaSpecificationExecutor<Push>{
}
