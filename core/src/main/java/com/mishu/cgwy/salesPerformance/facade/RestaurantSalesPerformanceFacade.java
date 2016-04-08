package com.mishu.cgwy.salesPerformance.facade;

import com.mishu.cgwy.response.query.QueryResponse;
import com.mishu.cgwy.salesPerformance.domain.RestaurantSalesPerformance;
import com.mishu.cgwy.salesPerformance.request.SalesPerformanceRequest;
import com.mishu.cgwy.salesPerformance.response.RestaurantSalesPerformanceWrapper;
import com.mishu.cgwy.salesPerformance.service.RestaurantSalesPerformanceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by xiao1zhao2 on 15/12/15.
 */
@Service
public class RestaurantSalesPerformanceFacade {

    @Autowired
    private RestaurantSalesPerformanceService restaurantSalesPerformanceService;

    @Transactional
    public void dailyCountRestaurantSalesPerformance(SalesPerformanceRequest request) {

        Date start = request.getStartDate();
        Date end = request.getEndDate();
        Map<Long, Integer> ordersMap = restaurantSalesPerformanceService.dailyCountOrders(start, end);
        Map<Long, BigDecimal> salesAmountMap = restaurantSalesPerformanceService.dailyCountSalesAmount(start, end);
        Map<Long, BigDecimal> avgCostAmountMap = restaurantSalesPerformanceService.dailyCountAvgCostAmount(start, end);

        Map<Long, RestaurantSalesPerformance> salesPerformanceMap = new HashMap<>();
        for (Long restaurantId : ordersMap.keySet()) {
            if (!salesPerformanceMap.containsKey(restaurantId)) {
                RestaurantSalesPerformance salesPerformance = new RestaurantSalesPerformance();
                salesPerformance.setOrders(ordersMap.get(restaurantId));
                salesPerformanceMap.put(restaurantId, salesPerformance);
            } else {
                salesPerformanceMap.get(restaurantId).setOrders(ordersMap.get(restaurantId));
            }
        }
        for (Long restaurantId : salesAmountMap.keySet()) {
            if (!salesPerformanceMap.containsKey(restaurantId)) {
                RestaurantSalesPerformance salesPerformance = new RestaurantSalesPerformance();
                salesPerformance.setSalesAmount(salesAmountMap.get(restaurantId));
                salesPerformanceMap.put(restaurantId, salesPerformance);
            } else {
                salesPerformanceMap.get(restaurantId).setSalesAmount(salesAmountMap.get(restaurantId));
            }
        }
        for (Long restaurantId : avgCostAmountMap.keySet()) {
            if (!salesPerformanceMap.containsKey(restaurantId)) {
                RestaurantSalesPerformance salesPerformance = new RestaurantSalesPerformance();
                salesPerformance.setAvgCostAmount(avgCostAmountMap.get(restaurantId));
                salesPerformanceMap.put(restaurantId, salesPerformance);
            } else {
                salesPerformanceMap.get(restaurantId).setAvgCostAmount(avgCostAmountMap.get(restaurantId));
            }
        }
        for (Long restaurantId : salesPerformanceMap.keySet()) {
            if (restaurantSalesPerformanceService.getByRestaurantIdAndDate(restaurantId, start).isEmpty()) {
                RestaurantSalesPerformance salesPerformance = salesPerformanceMap.get(restaurantId);
                salesPerformance.setRestaurant(restaurantSalesPerformanceService.getRestaurantById(restaurantId));
                salesPerformance.setDate(start);
                restaurantSalesPerformanceService.saveRestaurantSalesPerformance(salesPerformance);
            }
        }

    }

    @Transactional(readOnly = true)
    public QueryResponse<RestaurantSalesPerformanceWrapper> getRestaurantSalesPerformanceList(SalesPerformanceRequest request) {
        Page<RestaurantSalesPerformanceWrapper> page = restaurantSalesPerformanceService.getRestaurantSalesPerformance(request);
        QueryResponse<RestaurantSalesPerformanceWrapper> res = new QueryResponse<>();
        res.setContent(page.getContent());
        res.setPage(request.getPage());
        res.setPageSize(request.getPageSize());
        res.setTotal(page.getTotalElements());
        return res;
    }
}
