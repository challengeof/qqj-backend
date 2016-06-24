package com.qqj.org.repository;

import com.qqj.org.domain.RegisterTask;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface RegisterTaskRepository extends JpaRepository<RegisterTask, Long>, JpaSpecificationExecutor<RegisterTask> {
}
