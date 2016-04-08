package com.mishu.cgwy.profile.repository;

import com.mishu.cgwy.profile.domain.CustomerHint;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;


public interface CustomerHintRepository extends JpaRepository<CustomerHint, Long> {
    public List<CustomerHint> findByCustomerId(Long customerId);

}
