package com.mishu.cgwy.product.service;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.Assert.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"/application-persist.xml", "/application-context.xml", "/application-security.xml"})
public class SkuSalesStatisticsServiceTest {

    @Autowired
    private SkuSalesStatisticsService skuSalesStatisticsService;

    @Test
    @Transactional
    @Rollback(false)
    public void testRefreshSkuSalesStatistics() throws Exception {
        skuSalesStatisticsService.refreshSkuSalesStatistics();
    }
}