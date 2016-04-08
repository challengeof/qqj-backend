package com.mishu.cgwy.bonus.service;

import com.mishu.cgwy.admin.domain.AdminUser;
import com.mishu.cgwy.bonus.domain.CustomerServiceStatistics;
import com.mishu.cgwy.bonus.repository.CustomerServiceStatisticsRepository;
import com.mishu.cgwy.profile.domain.Restaurant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * User: xudong
 * Date: 5/29/15
 * Time: 11:32 AM
 */
@Service
public class CustomerServiceStatisticsService {
    @Autowired
    private CustomerServiceStatisticsRepository customerServiceStatisticsRepository;

    @Transactional(readOnly = true)
    public CustomerServiceStatistics findByMonthAndAdminUser(Date month, AdminUser adminUser) {

        final List<CustomerServiceStatistics> list = customerServiceStatisticsRepository.findByMonthAndAdminUser(month,
                adminUser);

        if (list.isEmpty()) {
            return null;
        } else {
            return list.get(0);
        }
    }

    @Transactional(readOnly = true)
    public List<CustomerServiceStatistics> findByMonth(Date month) {
        return customerServiceStatisticsRepository.findByMonth(month);

    }

    @Transactional
    public CustomerServiceStatistics saveCustomerServiceStatistics(CustomerServiceStatistics customerServiceStatistics) {
        return customerServiceStatisticsRepository.save(customerServiceStatistics);
    }

    public Map<Restaurant, Long> getComplaintCountGroupByAdminUser(Date start, Date end) {
        return null;
    }

    public CustomerServiceStatistics findByAdminUserAndMonth(AdminUser adminUser, Date month) {

        return customerServiceStatisticsRepository.findByAdminUserAndMonth(adminUser, month);
    }
}
