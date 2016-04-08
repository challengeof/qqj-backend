package com.mishu.cgwy.profile.repository;

import com.mishu.cgwy.profile.domain.Caller;
import com.mishu.cgwy.profile.domain.Customer;
import org.springframework.data.annotation.QueryAnnotation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

/**
 * Created by king-ck on 2015/9/29.
 */
public interface CallerRepository extends JpaRepository<Caller, Long>, JpaSpecificationExecutor<Caller> {


    Caller findByPhone(String phone);




}
