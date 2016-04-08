package com.mishu.cgwy.salesPerformance.facade;

import com.mishu.cgwy.admin.domain.AdminUser;
import com.mishu.cgwy.common.repository.CityRepository;
import com.mishu.cgwy.salesPerformance.request.SalesPerformanceRequest;
import com.mishu.cgwy.salesPerformance.service.BlockSalesPerformanceService;
import com.mishu.cgwy.salesPerformance.service.RestaurantSalesPerformanceService;
import com.mishu.cgwy.salesPerformance.service.SellerSalesPerformanceService;
import com.mishu.cgwy.task.util.ExportExcelUtils;
import org.apache.commons.lang.time.DateFormatUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by admin on 15/9/22.
 */
@Service
public class SalesPerformanceExportFacade {

    @Autowired
    private SellerSalesPerformanceService sellerSalesPerformanceService;
    @Autowired
    private RestaurantSalesPerformanceService restaurantSalesPerformanceService;
    @Autowired
    private BlockSalesPerformanceService blockSalesPerformanceService;
    @Autowired
    private CityRepository cityRepository;

    private static final String SALES_PERFORMANCE_SELLER_LIST = "/template/salesPerformance-seller-list.xls";
    private static final String SALES_PERFORMANCE_RESTAURANT_LIST = "/template/salesPerformance-restaurant-list.xls";
    private static final String SALES_PERFORMANCE_BLOCK_LIST = "/template/salesPerformance-block-list.xls";

    @Transactional(readOnly = true)
    public HttpEntity<byte[]> exportSellerSalesPerformance(SalesPerformanceRequest request, AdminUser operator) throws Exception {

        request.setPage(0);
        request.setPageSize(Integer.MAX_VALUE);
        Map<String, Object> beans = new HashMap<>();
        beans.put("city", request.getCityId() == null ? "全部" : cityRepository.getOne(request.getCityId()).getName());
        beans.put("startDate", request.getStartDate());
        beans.put("endDate", request.getEndDate());
        beans.put("list", sellerSalesPerformanceService.getSellerSalesPerformance(request).getContent());
        beans.put("now", new Date());
        beans.put("operator", operator.getRealname());
        String fileName = String.format("sellerSalesPerformance-%s.xls", DateFormatUtils.format(new Date(), "yyyy-MM-dd"));

        return ExportExcelUtils.generateExcelBytes(beans, fileName, SALES_PERFORMANCE_SELLER_LIST);
    }

    @Transactional(readOnly = true)
    public HttpEntity<byte[]> exportRestaurantSalesPerformance(SalesPerformanceRequest request, AdminUser operator) throws Exception {

        request.setPage(0);
        request.setPageSize(Integer.MAX_VALUE);
        Map<String, Object> beans = new HashMap<>();
        beans.put("city", request.getCityId() == null ? "全部" : cityRepository.getOne(request.getCityId()).getName());
        beans.put("startDate", request.getStartDate());
        beans.put("endDate", request.getEndDate());
        beans.put("list", restaurantSalesPerformanceService.getRestaurantSalesPerformance(request).getContent());
        beans.put("now", new Date());
        beans.put("operator", operator.getRealname());
        String fileName = String.format("restaurantSalesPerformance-%s.xls", DateFormatUtils.format(new Date(), "yyyy-MM-dd"));

        return ExportExcelUtils.generateExcelBytes(beans, fileName, SALES_PERFORMANCE_RESTAURANT_LIST);
    }

    @Transactional(readOnly = true)
    public HttpEntity<byte[]> exportBlockSalesPerformance(SalesPerformanceRequest request, AdminUser operator) throws Exception {

        request.setPage(0);
        request.setPageSize(Integer.MAX_VALUE);
        Map<String, Object> beans = new HashMap<>();
        beans.put("city", request.getCityId() == null ? "全部" : cityRepository.getOne(request.getCityId()).getName());
        beans.put("startDate", request.getStartDate());
        beans.put("endDate", request.getEndDate());
        beans.put("list", blockSalesPerformanceService.getBlockSalesPerformance(request).getContent());
        beans.put("now", new Date());
        beans.put("operator", operator.getRealname());
        String fileName = String.format("blockSalesPerformance-%s.xls", DateFormatUtils.format(new Date(), "yyyy-MM-dd"));

        return ExportExcelUtils.generateExcelBytes(beans, fileName, SALES_PERFORMANCE_BLOCK_LIST);
    }

}
