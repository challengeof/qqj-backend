package com.mishu.cgwy.common.repository;

import com.mishu.cgwy.profile.domain.Suggestion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

/**
 * Created by bowen on 15-5-26.
 */
public interface SuggestionRepository extends JpaRepository<Suggestion,Long> , JpaSpecificationExecutor<Suggestion> {
}
