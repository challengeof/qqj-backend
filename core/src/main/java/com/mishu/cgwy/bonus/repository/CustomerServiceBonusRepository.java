package com.mishu.cgwy.bonus.repository;

import com.mishu.cgwy.admin.domain.AdminPermission;
import com.mishu.cgwy.bonus.domain.CustomerServiceBonus;
import com.mishu.cgwy.profile.domain.Restaurant;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Date;
import java.util.List;


/**
 * User: xudong
 * Date: 12/2/14
 * Time: 3:26 PM
 */
public interface CustomerServiceBonusRepository extends JpaRepository<CustomerServiceBonus, Long> {

    List<CustomerServiceBonus> findByMonthAndWeekOfMonthAndRestaurantAndBonusType(Date month, int weekOfMonth,
                                                                                  Restaurant restaurant, int bonusType);

    List<CustomerServiceBonus> findByRestaurantAndBonusType(Restaurant restaurant, int bonusType);

    List<CustomerServiceBonus> findByMonthAndAdminUserId(Date month, Long adminUserId);
}
