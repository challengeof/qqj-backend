package com.mishu.cgwy.bonus.service;

import com.mishu.cgwy.bonus.domain.CustomerServiceBonus;
import com.mishu.cgwy.bonus.repository.CustomerServiceBonusRepository;
import com.mishu.cgwy.profile.domain.Restaurant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

/**
 * User: xudong
 * Date: 5/29/15
 * Time: 11:32 AM
 */
@Service
public class CustomerServiceBonusService {
    @Autowired
    private CustomerServiceBonusRepository customerServiceBonusRepository;

    @Transactional
    public CustomerServiceBonus findByMonthAndWeekOfMonthAndRestaurantAndBonusType(Date month,
                                                                                   int weekOfMonth,
                                                                                   Restaurant restaurant,
                                                                                   int bonusType) {

        final List<CustomerServiceBonus> list = customerServiceBonusRepository.findByMonthAndWeekOfMonthAndRestaurantAndBonusType(month, weekOfMonth,
                restaurant, bonusType);

        if (list.isEmpty()) {
            return null;
        } else {
            return list.get(0);
        }
    }

    @Transactional
    public List<CustomerServiceBonus> findByMonthAndAdminUserId(Date month, Long adminUserId) {
        return customerServiceBonusRepository.findByMonthAndAdminUserId(month,adminUserId);

    }

    @Transactional
    public List<CustomerServiceBonus> findByRestaurantAndBonusType(Restaurant restaurant,
                                                                   int bonusType) {
        return customerServiceBonusRepository.findByRestaurantAndBonusType(
                restaurant, bonusType);

    }

    @Transactional
    public CustomerServiceBonus saveCustomerServiceBonus(CustomerServiceBonus customerServiceBonus) {
        return customerServiceBonusRepository.save(customerServiceBonus);
    }
}
