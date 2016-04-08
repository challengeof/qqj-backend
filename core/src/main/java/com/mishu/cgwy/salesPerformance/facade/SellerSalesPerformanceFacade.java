package com.mishu.cgwy.salesPerformance.facade;

import com.mishu.cgwy.response.query.QueryResponse;
import com.mishu.cgwy.salesPerformance.domain.SellerSalesPerformance;
import com.mishu.cgwy.salesPerformance.request.SalesPerformanceRequest;
import com.mishu.cgwy.salesPerformance.response.SellerSalesPerformanceWrapper;
import com.mishu.cgwy.salesPerformance.service.SellerSalesPerformanceService;
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
public class SellerSalesPerformanceFacade {

    @Autowired
    private SellerSalesPerformanceService sellerSalesPerformanceService;

    @Transactional
    public void dailyCountSellerSalesPerformance(SalesPerformanceRequest request) {

        Date start = request.getStartDate();
        Date end = request.getEndDate();
        Map<Long, Integer> newCustomersMap = sellerSalesPerformanceService.dailyCountNewCustomers(start, end);
        Map<Long, Integer> ordersMap = sellerSalesPerformanceService.dailyCountOrders(start, end);
        Map<Long, BigDecimal> salesAmountMap = sellerSalesPerformanceService.dailyCountSalesAmount(start, end);
        Map<Long, BigDecimal> avgCostAmountMap = sellerSalesPerformanceService.dailyCountAvgCostAmount(start, end);

        Map<Long, SellerSalesPerformance> salesPerformanceMap = new HashMap<>();
        for (Long sellerId : newCustomersMap.keySet()) {
            if (!salesPerformanceMap.containsKey(sellerId)) {
                SellerSalesPerformance salesPerformance = new SellerSalesPerformance();
                salesPerformance.setNewCustomers(newCustomersMap.get(sellerId));
                salesPerformanceMap.put(sellerId, salesPerformance);
            } else {
                salesPerformanceMap.get(sellerId).setNewCustomers(newCustomersMap.get(sellerId));
            }
        }
        for (Long sellerId : ordersMap.keySet()) {
            if (!salesPerformanceMap.containsKey(sellerId)) {
                SellerSalesPerformance salesPerformance = new SellerSalesPerformance();
                salesPerformance.setOrders(ordersMap.get(sellerId));
                salesPerformanceMap.put(sellerId, salesPerformance);
            } else {
                salesPerformanceMap.get(sellerId).setOrders(ordersMap.get(sellerId));
            }
        }
        for (Long sellerId : salesAmountMap.keySet()) {
            if (!salesPerformanceMap.containsKey(sellerId)) {
                SellerSalesPerformance salesPerformance = new SellerSalesPerformance();
                salesPerformance.setSalesAmount(salesAmountMap.get(sellerId));
                salesPerformanceMap.put(sellerId, salesPerformance);
            } else {
                salesPerformanceMap.get(sellerId).setSalesAmount(salesAmountMap.get(sellerId));
            }
        }
        for (Long sellerId : avgCostAmountMap.keySet()) {
            if (!salesPerformanceMap.containsKey(sellerId)) {
                SellerSalesPerformance salesPerformance = new SellerSalesPerformance();
                salesPerformance.setAvgCostAmount(avgCostAmountMap.get(sellerId));
                salesPerformanceMap.put(sellerId, salesPerformance);
            } else {
                salesPerformanceMap.get(sellerId).setAvgCostAmount(avgCostAmountMap.get(sellerId));
            }
        }
        for (Long sellerId : salesPerformanceMap.keySet()) {
            if (sellerSalesPerformanceService.getByAdminUserIdAndDate(sellerId, start).isEmpty()) {
                SellerSalesPerformance salesPerformance = salesPerformanceMap.get(sellerId);
                salesPerformance.setAdminUser(sellerSalesPerformanceService.getAdminUserById(sellerId));
                salesPerformance.setDate(start);
                sellerSalesPerformanceService.saveSellerSalesPerformance(salesPerformance);
            }
        }

    }

    @Transactional(readOnly = true)
    public QueryResponse<SellerSalesPerformanceWrapper> getSellerSalesPerformanceList(SalesPerformanceRequest request) {
        Page<SellerSalesPerformanceWrapper> page = sellerSalesPerformanceService.getSellerSalesPerformance(request);
        QueryResponse<SellerSalesPerformanceWrapper> res = new QueryResponse<>();
        res.setContent(page.getContent());
        res.setPage(request.getPage());
        res.setPageSize(request.getPageSize());
        res.setTotal(page.getTotalElements());
        return res;
    }
}
