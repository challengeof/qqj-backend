package com.mishu.cgwy.salesPerformance.facade;

import com.mishu.cgwy.response.query.QueryResponse;
import com.mishu.cgwy.salesPerformance.domain.BlockSalesPerformance;
import com.mishu.cgwy.salesPerformance.request.SalesPerformanceRequest;
import com.mishu.cgwy.salesPerformance.response.BlockSalesPerformanceWrapper;
import com.mishu.cgwy.salesPerformance.service.BlockSalesPerformanceService;
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
public class BlockSalesPerformanceFacade {

    @Autowired
    private BlockSalesPerformanceService blockSalesPerformanceService;

    @Transactional
    public void dailyCountBlockSalesPerformance(SalesPerformanceRequest request) {

        Date start = request.getStartDate();
        Date end = request.getEndDate();
        Map<Long, Integer> newCustomersMap = blockSalesPerformanceService.dailyCountNewCustomers(start, end);
        Map<Long, Integer> ordersMap = blockSalesPerformanceService.dailyCountOrders(start, end);
        Map<Long, BigDecimal> salesAmountMap = blockSalesPerformanceService.dailyCountSalesAmount(start, end);
        Map<Long, BigDecimal> avgCostAmountMap = blockSalesPerformanceService.dailyCountAvgCostAmount(start, end);

        Map<Long, BlockSalesPerformance> salesPerformanceMap = new HashMap<>();
        for (Long blockId : newCustomersMap.keySet()) {
            if (!salesPerformanceMap.containsKey(blockId)) {
                BlockSalesPerformance salesPerformance = new BlockSalesPerformance();
                salesPerformance.setNewCustomers(newCustomersMap.get(blockId));
                salesPerformanceMap.put(blockId, salesPerformance);
            } else {
                salesPerformanceMap.get(blockId).setNewCustomers(newCustomersMap.get(blockId));
            }
        }
        for (Long blockId : ordersMap.keySet()) {
            if (!salesPerformanceMap.containsKey(blockId)) {
                BlockSalesPerformance salesPerformance = new BlockSalesPerformance();
                salesPerformance.setOrders(ordersMap.get(blockId));
                salesPerformanceMap.put(blockId, salesPerformance);
            } else {
                salesPerformanceMap.get(blockId).setOrders(ordersMap.get(blockId));
            }
        }
        for (Long blockId : salesAmountMap.keySet()) {
            if (!salesPerformanceMap.containsKey(blockId)) {
                BlockSalesPerformance salesPerformance = new BlockSalesPerformance();
                salesPerformance.setSalesAmount(salesAmountMap.get(blockId));
                salesPerformanceMap.put(blockId, salesPerformance);
            } else {
                salesPerformanceMap.get(blockId).setSalesAmount(salesAmountMap.get(blockId));
            }
        }
        for (Long blockId : avgCostAmountMap.keySet()) {
            if (!salesPerformanceMap.containsKey(blockId)) {
                BlockSalesPerformance salesPerformance = new BlockSalesPerformance();
                salesPerformance.setAvgCostAmount(avgCostAmountMap.get(blockId));
                salesPerformanceMap.put(blockId, salesPerformance);
            } else {
                salesPerformanceMap.get(blockId).setAvgCostAmount(avgCostAmountMap.get(blockId));
            }
        }
        for (Long blockId : salesPerformanceMap.keySet()) {
            if (blockSalesPerformanceService.getByBlockIdAndDate(blockId, start).isEmpty()) {
                BlockSalesPerformance salesPerformance = salesPerformanceMap.get(blockId);
                salesPerformance.setBlock(blockSalesPerformanceService.getBlockById(blockId));
                salesPerformance.setDate(start);
                blockSalesPerformanceService.saveBlockSalesPerformance(salesPerformance);
            }
        }

    }

    @Transactional(readOnly = true)
    public QueryResponse<BlockSalesPerformanceWrapper> getBlockSalesPerformanceList(SalesPerformanceRequest request) {
        Page<BlockSalesPerformanceWrapper> page = blockSalesPerformanceService.getBlockSalesPerformance(request);
        QueryResponse<BlockSalesPerformanceWrapper> res = new QueryResponse<>();
        res.setContent(page.getContent());
        res.setPage(request.getPage());
        res.setPageSize(request.getPageSize());
        res.setTotal(page.getTotalElements());
        return res;
    }
}
