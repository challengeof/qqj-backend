package com.mishu.cgwy.task.repository;

import com.mishu.cgwy.admin.domain.AdminUser;
import com.mishu.cgwy.task.domain.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Date;
import java.util.List;

public interface TaskRepository extends JpaRepository<Task, Long>, JpaSpecificationExecutor<Task> {
    List<Task> findBySubmitUserOrderByIdDesc(AdminUser adminUser);

    List<Task> findBySubmitDateLessThan(Date date);
}
