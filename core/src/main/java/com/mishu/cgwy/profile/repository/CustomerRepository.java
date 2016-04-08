package com.mishu.cgwy.profile.repository;

import com.mishu.cgwy.profile.domain.Customer;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Collection;
import java.util.List;


/**
 * User: xudong
 * Date: 12/2/14
 * Time: 3:26 PM
 */
public interface CustomerRepository extends JpaRepository<Customer, Long>, JpaSpecificationExecutor<Customer> {
    List<Customer> findByUsername(String username);
    
    List<Customer> findByAdminUserId(Long adminUserId);

    List<Customer> findByIdIn(Collection<Long> userList);
    
}
