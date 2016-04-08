package com.mishu.cgwy.bonus.facade;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;
import com.mishu.cgwy.admin.domain.AdminRole;
import com.mishu.cgwy.admin.domain.AdminUser;
import com.mishu.cgwy.admin.dto.AdminUserQueryRequest;
import com.mishu.cgwy.admin.facade.AdminUserFacade;
import com.mishu.cgwy.admin.service.AdminUserService;
import com.mishu.cgwy.admin.vo.AdminRoleVo;
import com.mishu.cgwy.admin.vo.AdminUserVo;
import com.mishu.cgwy.bonus.controller.SalesmanStatistics;
import com.mishu.cgwy.bonus.controller.SalesmanStatisticsRequest;
import com.mishu.cgwy.bonus.domain.CustomerServiceBonus;
import com.mishu.cgwy.bonus.domain.CustomerServiceStatistics;
import com.mishu.cgwy.bonus.service.CustomerServiceBonusService;
import com.mishu.cgwy.bonus.service.CustomerServiceStatisticsService;
import com.mishu.cgwy.bonus.vo.CustomerServiceStatisticsVo;
import com.mishu.cgwy.order.constants.OrderStatus;
import com.mishu.cgwy.order.controller.OrderQueryRequest;
import com.mishu.cgwy.order.service.OrderService;
import com.mishu.cgwy.product.constants.RestaurantStatus;
import com.mishu.cgwy.profile.controller.RestaurantQueryRequest;
import com.mishu.cgwy.profile.domain.Restaurant;
import com.mishu.cgwy.profile.service.CustomerService;
import com.mishu.cgwy.profile.service.RestaurantService;
import com.mishu.cgwy.saleVisit.service.SaleVisitService;
import org.apache.commons.lang.time.DateUtils;
import org.joda.time.DateTime;
import org.joda.time.Days;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.*;

/**
 * User: xudong
 * Date: 5/29/15
 * Time: 11:35 AM
 */
@Service
public class CustomerServiceStatisticsFacade {
    @Autowired
    private CustomerServiceBonusService customerServiceBonusService;

    @Autowired
    private CustomerServiceStatisticsService customerServiceStatisticsService;

    @Autowired
    private AdminUserService adminUserService;

    @Autowired
    private CustomerService customerService;

    @Autowired
    private OrderService orderService;

    @Autowired
    private AdminUserFacade adminUserFacade;

    @Autowired
    private RestaurantService restaurantService;

    @Autowired
    private SaleVisitService saleVisitService;

    @Transactional
    public void refreshBonus(Date date) {


        Date month = DateUtils.truncate(date, Calendar.MONTH);



        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);

        int weekOfMonth = (calendar.get(Calendar.DAY_OF_MONTH) - 1) / 7 + 1;

        Date start = DateUtils.setDays(DateUtils.truncate(month, Calendar.MONTH),
                 (weekOfMonth - 1) * 7 + 1);
        final int dayOfEnd = (weekOfMonth) * 7 + 1;

        final int actualMaximum = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);

        Date end = null;
        if (dayOfEnd > actualMaximum)  {
            end = DateUtils.addMonths(month, 1);
        } else {
            end = DateUtils.setDays(start, dayOfEnd);
        }

        RestaurantQueryRequest newRestaurantRequest = new RestaurantQueryRequest();
        newRestaurantRequest.setStart(start);
        newRestaurantRequest.setEnd(end);
        newRestaurantRequest.setPage(0);
        newRestaurantRequest.setStatus(RestaurantStatus.ACTIVE.getValue());
        newRestaurantRequest.setPageSize(Integer.MAX_VALUE);
        newRestaurantRequest.setStatus(RestaurantStatus.ACTIVE.getValue());
        final Page<Restaurant> newRestaurants = customerService.findRestaurants(newRestaurantRequest, null);

        for (Restaurant r : newRestaurants) {
            CustomerServiceBonus registerBonus = customerServiceBonusService.findByMonthAndWeekOfMonthAndRestaurantAndBonusType(month, weekOfMonth, r,
                    CustomerServiceBonus.RegisterBonus);

            if (registerBonus == null) {
                registerBonus = new CustomerServiceBonus();
            }
            registerBonus.setAdminUser(r.getCustomer().getAdminUser());

            // 餐馆注册奖励0元
            registerBonus.setBonus(BigDecimal.valueOf(0));
            registerBonus.setBonusType(CustomerServiceBonus.RegisterBonus);
            registerBonus.setMonth(month);
            registerBonus.setWeekOfMonth(weekOfMonth);
            registerBonus.setRestaurant(r);

            customerServiceBonusService.saveCustomerServiceBonus(registerBonus);
        }

        final Map<AdminUser, Map<Restaurant, BigDecimal>> restaurantConsumptionStatistics = orderService.getRestaurantConsumptionStatistics(start, end);
        for (Map.Entry<AdminUser, Map<Restaurant, BigDecimal>> consumption : restaurantConsumptionStatistics.entrySet()) {

            for (Map.Entry<Restaurant, BigDecimal> entry : consumption.getValue().entrySet()) {


                if (entry.getValue().compareTo(BigDecimal.valueOf(300)) >= 0) {
                    // 餐馆周消费满300奖励
                    final Restaurant restaurant = entry.getKey();
                    final int days = Days.daysBetween(
                            new DateTime(restaurant.getCreateTime()),
                            new DateTime(new Date())
                    ).getDays();


                    // 注册时间超过80天不奖励
                    if (days > 80) {
                        continue;
                    } else if (customerServiceBonusService.findByRestaurantAndBonusType(restaurant,
                            CustomerServiceBonus.ConsumptionBonus).size() > 1) {
                        // 奖励最多2次
                        continue;
                    } else {
                        CustomerServiceBonus consumptionBonus = customerServiceBonusService
                                .findByMonthAndWeekOfMonthAndRestaurantAndBonusType(month, weekOfMonth, restaurant,
                                        CustomerServiceBonus.ConsumptionBonus);

                        if (consumptionBonus == null) {
                            consumptionBonus = new CustomerServiceBonus();
                        }

                        consumptionBonus.setAdminUser(consumption.getKey());
                        // 周消费超过300奖励28
                        consumptionBonus.setBonus(BigDecimal.valueOf(0));
                        consumptionBonus.setBonusType(CustomerServiceBonus.ConsumptionBonus);
                        consumptionBonus.setMonth(month);
                        consumptionBonus.setWeekOfMonth(weekOfMonth);
                        consumptionBonus.setRestaurant(restaurant);

                        customerServiceBonusService.saveCustomerServiceBonus(consumptionBonus);
                    }
                }
            }
        }
    }

    @Transactional
    public void refreshStatistics(Date month) {
        month = DateUtils.truncate(month, Calendar.MONTH);

        AdminRole customerServiceRole = null;

        for (AdminRole role : adminUserService.getAdminRoles()) {
            if (role.getName().equals(AdminRole.CustomerService)) {
                customerServiceRole = role;
                break;
            }
        }

        if (customerServiceRole == null) {
            return;
        }

        final List<AdminUser> adminUsers = adminUserService.getAdminUsersByRole(customerServiceRole);

        Date start = DateUtils.truncate(month, Calendar.DAY_OF_MONTH);
        Date end = DateUtils.addMonths(start, 1);

        OrderQueryRequest request = new OrderQueryRequest();
        request.setStart(start);
        request.setEnd(end);
        request.setPage(0);
        request.setPageSize(Integer.MAX_VALUE);

        final List<CustomerServiceStatistics> statistics = customerServiceStatisticsService.findByMonth(month);
        for (CustomerServiceStatistics s : statistics) {
            s.setBonus(BigDecimal.ZERO);
            s.setComplaintCount(0);
            s.setConsumption(BigDecimal.ZERO);
            s.setRestaurantHavingOrderCount(0);
            s.setNewRestaurantCount(0);
        }

        Map<Long, Long> complaintCountGroupByAdminUser = customerService.getComplaintCountGroupByAdminUser(start, end);
        Map<Long, BigDecimal> consumptionGroupByAdminUser = orderService.getConsumptionGroupByAdminUser(start, end);
        Map<Long, Long> restaurantHavingOrderCountGroupByAdminUser = orderService.getRestaurantHavingOrderCountGroupByAdminUser
                (start, end);
        Map<Long, Long> newRestaurantCountGroupByAdminUser = customerService.getNewRestaurantCountGroupByAdminUser(start, end);


        for (AdminUser adminUser : adminUsers) {
            CustomerServiceStatistics byMonthAndAdminUser = customerServiceStatisticsService.findByMonthAndAdminUser(month, adminUser);
            if (byMonthAndAdminUser == null) {
                byMonthAndAdminUser = new CustomerServiceStatistics();
                byMonthAndAdminUser.setMonth(month);
                byMonthAndAdminUser.setAdminUser(adminUser);
            }

            if (complaintCountGroupByAdminUser.containsKey(adminUser.getId())) {
                byMonthAndAdminUser.setComplaintCount(complaintCountGroupByAdminUser.get(adminUser.getId()));
            }

            if (consumptionGroupByAdminUser.containsKey(adminUser.getId())) {
                byMonthAndAdminUser.setConsumption(consumptionGroupByAdminUser.get(adminUser.getId()));
            }

            if (restaurantHavingOrderCountGroupByAdminUser.containsKey(adminUser.getId())) {
                byMonthAndAdminUser.setRestaurantHavingOrderCount(restaurantHavingOrderCountGroupByAdminUser.get(adminUser.getId()));
            }

            if (newRestaurantCountGroupByAdminUser.containsKey(adminUser.getId())) {
                byMonthAndAdminUser.setNewRestaurantCount(newRestaurantCountGroupByAdminUser.get(adminUser.getId()));
            }

            List<CustomerServiceBonus> bonuses = customerServiceBonusService.findByMonthAndAdminUserId(month,
                    adminUser.getId());

            BigDecimal totalBonus = BigDecimal.ZERO;
            for (CustomerServiceBonus b : bonuses) {
                totalBonus = totalBonus.add(b.getBonus());
            }

            byMonthAndAdminUser.setBonus(totalBonus);

            customerServiceStatisticsService.saveCustomerServiceStatistics(byMonthAndAdminUser);
        }
    }


    public List<CustomerServiceStatisticsVo> getCustomerServiceStatistics(Date month) {
        final ArrayList<CustomerServiceStatisticsVo> list = new ArrayList<>(
                Collections2.transform(
                        customerServiceStatisticsService.findByMonth(DateUtils.truncate(month, Calendar.MONTH)),
                        new Function<CustomerServiceStatistics, CustomerServiceStatisticsVo>() {
                            @Override
                            public CustomerServiceStatisticsVo apply(CustomerServiceStatistics input) {
                                CustomerServiceStatisticsVo vo = new CustomerServiceStatisticsVo();
                                vo.setId(input.getId());
                                vo.setMonth(input.getMonth());
                                vo.setBonus(input.getBonus());
                                vo.setNewRestaurantCount(input.getNewRestaurantCount());
                                vo.setRestaurantHavingOrderCount(input.getRestaurantHavingOrderCount());
                                vo.setComplaintCount(input.getComplaintCount());
                                vo.setConsumption(input.getConsumption());

                                AdminUser adminUser = input.getAdminUser();
                                AdminUserVo adminUserVo = new AdminUserVo();
                                adminUserVo.setId(adminUser.getId());
                                adminUserVo.setUsername(adminUser.getUsername());
                                adminUserVo.setTelephone(adminUser.getTelephone());
                                adminUserVo.setEnabled(adminUser.isEnabled());
                                adminUserVo.setRealname(adminUser.getRealname());
                                adminUserVo.setGlobalAdmin(adminUser.isGlobalAdmin());
                                vo.setAdminUser(adminUserVo);
                                return vo;
                            }

                        }
                )
        );
        Collections.sort(list, new Comparator<CustomerServiceStatisticsVo>() {
            @Override
            public int compare(CustomerServiceStatisticsVo o1, CustomerServiceStatisticsVo o2) {
                return o1.getBonus().compareTo(o2.getBonus()) * -1;
            }
        });

        return list;
    }

    @Transactional(readOnly = true)
    public List<SalesmanStatistics> getSalesmanStatistics(SalesmanStatisticsRequest request , AdminUser operator) {
        List<SalesmanStatistics> salesmanStatisticses = new ArrayList<>();
        if (request.getAdminUserId() == null) {

            AdminUserQueryRequest adminUserQueryRequest = new AdminUserQueryRequest();
            adminUserQueryRequest.setRoleName("CustomerService");
            adminUserQueryRequest.setIsEnabled(true);
            List<AdminUserVo> adminUsers = new ArrayList<>(Collections2.filter(adminUserFacade.getSimpleAdminUsers(adminUserQueryRequest), new Predicate<AdminUserVo>() {
                @Override
                public boolean apply(AdminUserVo input) {
                    return input.isGlobalAdmin();
                }
            }));
            OrderQueryRequest orderQueryRequest = new OrderQueryRequest();
            if (request.getStart() != null) {
                orderQueryRequest.setStart(request.getStart());
            }
            if (request.getEnd() != null) {
                orderQueryRequest.setEnd(request.getEnd());
            }



            for (AdminUserVo adminUser : adminUsers) {

                orderQueryRequest.setAdminId(adminUser.getId());
                request.setAdminUserId(adminUser.getId());
                SalesmanStatistics salesmanStatistics = getSalesmanStatisticsByAdminUserId(request, orderQueryRequest, operator);
                salesmanStatistics.setAdminUser(adminUser);
                salesmanStatisticses.add(salesmanStatistics);

            }

        } else {

            OrderQueryRequest orderQueryRequest = new OrderQueryRequest();
            if (request.getStart() != null) {
                orderQueryRequest.setStart(request.getStart());
            }
            if (request.getEnd() != null) {
                orderQueryRequest.setEnd(request.getEnd());
            }
            orderQueryRequest.setAdminId(request.getAdminUserId());
            SalesmanStatistics salesmanStatistics = getSalesmanStatisticsByAdminUserId(request, orderQueryRequest, operator);
            AdminUser adminUser = adminUserService.getAdminUser(request.getAdminUserId());
            AdminUserVo adminUserVo = new AdminUserVo();
            adminUserVo.setId(adminUser.getId());
            adminUserVo.setUsername(adminUser.getUsername());
            adminUserVo.setTelephone(adminUser.getTelephone());
            adminUserVo.setEnabled(adminUser.isEnabled());
            adminUserVo.setRealname(adminUser.getRealname());
            adminUserVo.setGlobalAdmin(adminUser.isGlobalAdmin());

            if (!adminUserVo.isGlobalAdmin()) {
                adminUserVo.setOrganizationId(adminUser.getOrganizations().iterator().next().getId());
            }

            for (AdminRole role : adminUser.getAdminRoles()) {
                AdminRoleVo adminRoleVo = new AdminRoleVo();
                adminRoleVo.setId(role.getId());
                adminRoleVo.setName(role.getName());
                adminRoleVo.setDisplayName(role.getDisplayName());
                adminRoleVo.setOrganizationRole(role.isOrganizationRole());
                adminUserVo.getAdminRoles().add(adminRoleVo);
            }
            salesmanStatistics.setAdminUser(adminUserVo);
            salesmanStatisticses.add(salesmanStatistics);

        }
        return salesmanStatisticses;
    }

    private SalesmanStatistics getSalesmanStatisticsByAdminUserId(SalesmanStatisticsRequest request, OrderQueryRequest orderQueryRequest, AdminUser operator) {
        SalesmanStatistics salesmanStatistics = new SalesmanStatistics();
        orderQueryRequest.setStatus(OrderStatus.COMMITTED.getValue());
        Long orderQuantity = orderService.restaurantCount(orderQueryRequest, operator);
        salesmanStatistics.setOrderQuantity(orderQuantity.intValue());

        Long deliveryQuantity = orderService.getDeliveryCount(request);
        orderQueryRequest.setStatus(null);
        salesmanStatistics.setDeliveryQuantity(deliveryQuantity.intValue());

        orderQueryRequest.setRefundsIsNotEmpty(true);
        Long refundQuantity = orderService.restaurantCount(orderQueryRequest, operator);
        orderQueryRequest.setRefundsIsNotEmpty(false);
        salesmanStatistics.setRefundQuantity(refundQuantity.intValue());


        Long newRestaurantQuantity = restaurantService.newRestaurantCount(request);
        salesmanStatistics.setNewRestaurantQuantity(newRestaurantQuantity.intValue());

//        Long visitQuantity = saleVisitService.getSaleVisitCount(request);
//        salesmanStatistics.setVisitQuantity(visitQuantity.intValue());
//
//        Long visitDistinctRestaurantQuantity = saleVisitService.getSaleVisitCountDistinct(request);
//        salesmanStatistics.setVisitDistinctRestaurantQuantity(visitDistinctRestaurantQuantity.intValue());

        return salesmanStatistics;
    }

}
