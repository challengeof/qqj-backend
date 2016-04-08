package com.mishu.cgwy.common.repository;

import com.mishu.cgwy.profile.domain.Caller;
import com.mishu.cgwy.profile.domain.Feedback;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

/**
 * Created by bowen on 15-5-26.
 */
public interface FeedbackRepository extends JpaRepository<Feedback,Long> , JpaSpecificationExecutor<Feedback> {
}
