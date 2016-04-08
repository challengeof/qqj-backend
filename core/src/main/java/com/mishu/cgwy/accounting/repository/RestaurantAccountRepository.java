package com.mishu.cgwy.accounting.repository;

import com.mishu.cgwy.accounting.domain.RestaurantAccount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

/**
 * Created by wangguodong on 15/10/13.
 */
public interface RestaurantAccountRepository extends JpaRepository<RestaurantAccount, Long>,JpaSpecificationExecutor<RestaurantAccount> {
}
