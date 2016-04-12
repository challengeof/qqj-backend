package com.mishu.cgwy.org.repository;

import com.mishu.cgwy.org.domain.Team;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface TeamRepository extends JpaRepository<Team, Long> , JpaSpecificationExecutor<Team>{
}
