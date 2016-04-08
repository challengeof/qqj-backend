package com.mishu.cgwy.bonus.repository;

import com.mishu.cgwy.admin.domain.AdminUser;
import com.mishu.cgwy.bonus.domain.CustomerServiceStatistics;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Date;
import java.util.List;


/**
 * User: xudong
 * Date: 12/2/14
 * Time: 3:26 PM
 */
public interface CustomerServiceStatisticsRepository extends JpaRepository<CustomerServiceStatistics, Long> {

    List<CustomerServiceStatistics> findByMonthAndAdminUser(Date month, AdminUser adminUser);

    List<CustomerServiceStatistics> findByMonth(Date month);


    CustomerServiceStatistics findByAdminUserAndMonth(AdminUser adminUser, Date month);
}
