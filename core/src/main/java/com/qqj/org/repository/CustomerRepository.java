package com.qqj.org.repository;

import com.qqj.org.domain.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface CustomerRepository extends JpaRepository<Customer, Long>, JpaSpecificationExecutor<Customer> {
    List<Customer> findByUsername(String username);

    @Modifying
    @Query("update Customer c set c.rightCode = c.rightCode + 2 where c.rightCode >= :rightCode and c.team.id = :teamId")
    void updateCustomerRightCode(Long rightCode, Long teamId);

    @Modifying
    @Query("update Customer c set c.leftCode = c.leftCode + 2 where c.leftCode >= :rightCode and c.team.id = :teamId")
    void updateCustomerLeftCode(Long rightCode, Long teamId);
}
