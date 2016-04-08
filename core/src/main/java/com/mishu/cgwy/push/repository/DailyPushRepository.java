package com.mishu.cgwy.push.repository;

import com.mishu.cgwy.push.domain.DailyPush;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface DailyPushRepository extends JpaRepository<DailyPush, Long>, JpaSpecificationExecutor<DailyPush> {

    DailyPush findByTag(String tag);
}
