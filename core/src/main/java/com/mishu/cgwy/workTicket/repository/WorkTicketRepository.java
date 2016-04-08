package com.mishu.cgwy.workTicket.repository;

import com.mishu.cgwy.admin.domain.AdminUser;
import com.mishu.cgwy.workTicket.domain.WorkTicket;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

/**
 * Created by bowen on 16/2/29.
 */
public interface WorkTicketRepository extends JpaRepository<WorkTicket, Long>, JpaSpecificationExecutor<WorkTicket> {
    List<WorkTicket> findByFollowUp(AdminUser followUp);
}
