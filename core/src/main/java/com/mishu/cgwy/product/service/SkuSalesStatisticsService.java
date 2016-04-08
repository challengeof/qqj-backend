package com.mishu.cgwy.product.service;

import com.google.common.base.Function;
import com.google.common.collect.Collections2;
import com.mishu.cgwy.order.constants.OrderStatus;
import com.mishu.cgwy.order.repository.OrderItemRepository;
import com.mishu.cgwy.product.domain.Sku;
import com.mishu.cgwy.product.domain.SkuSalesStatistics;
import com.mishu.cgwy.product.repository.SkuSalesStatisticsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * User: xudong
 * Date: 5/3/15
 * Time: 2:24 PM
 */
@Service
public class SkuSalesStatisticsService {
    @Autowired
    private SkuSalesStatisticsRepository skuSalesStatisticsRepository;

    @Autowired
    private OrderItemRepository orderItemRepository;

    @Transactional
    public void refreshSkuSalesStatistics() {
        skuSalesStatisticsRepository.deleteAll();

        for (SkuSalesStatistics tuple : groupSaleCountBySku()) {
            skuSalesStatisticsRepository.save(tuple);
        }
    }

    private List<SkuSalesStatistics> groupSaleCountBySku() {
        final List<Object[]> tuples = orderItemRepository.groupSaleCountBySku(OrderStatus.COMPLETED.getValue());
        return new ArrayList<>(Collections2.transform(tuples, new Function<Object[], SkuSalesStatistics>() {
            @Override
            public SkuSalesStatistics apply(Object[] input) {
                SkuSalesStatistics statistics = new SkuSalesStatistics();
                statistics.setSku((Sku) input[0]);
                statistics.setSalesCount(((Number) input[1]).longValue());
                return statistics;
            }
        }));
    }

    @Transactional
    public long getSaleCount(Long skuId) {
        final SkuSalesStatistics one = skuSalesStatisticsRepository.findOne(skuId);
        return one == null ? 0l : one.getSalesCount();
    }

    @Transactional
    public List<SkuSalesStatistics> getSaleCount(List<Long> skuId) {
        return skuSalesStatisticsRepository.findAll(skuId);
    }
}
