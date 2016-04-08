package com.mishu.cgwy.profile.repository;

import com.mishu.cgwy.profile.domain.RestaurantAlterLog;
import com.mishu.cgwy.profile.domain.RestaurantAuditReview;
import lombok.Data;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Collection;
import java.util.List;

/**
 * Created by king-ck on 2016/3/10.
 */
public interface RestaurantAuditReviewRepository extends JpaRepository<RestaurantAuditReview, Long>, JpaSpecificationExecutor<RestaurantAuditReview> {


    List<RestaurantAuditReview> findByIdIn(Collection<Long> userList);

}
