package com.mishu.cgwy.profile.repository;

import com.mishu.cgwy.profile.domain.Complaint;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ComplaintRepository extends JpaRepository<Complaint, Long> {

    public Complaint findByCustomerIdAndAdminId(Long customerId, Long adminId);


}
