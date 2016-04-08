package com.mishu.cgwy.organization;

import com.mishu.cgwy.organization.domain.Organization;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;


/**
 * Created by xingdong on 15/7/1.
 */
public interface OrganizationRepository extends JpaRepository<Organization,Long>, JpaSpecificationExecutor<Organization> {
}
