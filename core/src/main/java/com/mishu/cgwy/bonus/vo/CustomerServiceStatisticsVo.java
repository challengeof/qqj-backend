package com.mishu.cgwy.bonus.vo;

import com.mishu.cgwy.admin.vo.AdminUserVo;
import com.mishu.cgwy.bonus.domain.CustomerServiceStatistics;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Data
public class CustomerServiceStatisticsVo {
    private Long id;
    private Date month;
    private AdminUserVo adminUser;
    private BigDecimal bonus = BigDecimal.ZERO;
    private long newRestaurantCount = 0;
    private long restaurantHavingOrderCount = 0;
    private long complaintCount = 0;
    private BigDecimal consumption = BigDecimal.ZERO;
}
