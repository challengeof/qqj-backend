package com.mishu.cgwy.salesPerformance.controller;

import com.mishu.cgwy.admin.controller.CurrentAdminUser;
import com.mishu.cgwy.admin.domain.AdminUser;
import com.mishu.cgwy.response.query.QueryResponse;
import com.mishu.cgwy.salesPerformance.facade.BlockSalesPerformanceFacade;
import com.mishu.cgwy.salesPerformance.facade.RestaurantSalesPerformanceFacade;
import com.mishu.cgwy.salesPerformance.facade.SalesPerformanceExportFacade;
import com.mishu.cgwy.salesPerformance.facade.SellerSalesPerformanceFacade;
import com.mishu.cgwy.salesPerformance.request.SalesPerformanceRequest;
import com.mishu.cgwy.salesPerformance.response.BlockSalesPerformanceWrapper;
import com.mishu.cgwy.salesPerformance.response.RestaurantSalesPerformanceWrapper;
import com.mishu.cgwy.salesPerformance.response.SellerSalesPerformanceWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.http.HttpEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by xiao1zhao2 on 15/12/3.
 */
@Controller
public class SalesPerformanceController {

    @Autowired
    private SellerSalesPerformanceFacade sellerSalesPerformanceFacade;
    @Autowired
    private RestaurantSalesPerformanceFacade restaurantSalesPerformanceFacade;
    @Autowired
    private BlockSalesPerformanceFacade blockSalesPerformanceFacade;
    @Autowired
    private SalesPerformanceExportFacade salesPerformanceExportFacade;

    @InitBinder
    public void initBinder(WebDataBinder binder) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        dateFormat.setLenient(false);
        binder.registerCustomEditor(Date.class, new CustomDateEditor(dateFormat, false));
    }

    @RequestMapping(value = "api/salesPerformance/seller/dailyCount", method = RequestMethod.GET)
    @ResponseBody
    public void dailyCountSellerSalesPerformance(SalesPerformanceRequest request) {
        sellerSalesPerformanceFacade.dailyCountSellerSalesPerformance(request);
    }

    @RequestMapping(value = "api/salesPerformance/seller/list", method = RequestMethod.GET)
    @ResponseBody
    public QueryResponse<SellerSalesPerformanceWrapper> getSellerSalesPerformanceWrapperList(SalesPerformanceRequest request) {
        return sellerSalesPerformanceFacade.getSellerSalesPerformanceList(request);
    }

    @RequestMapping(value = "api/salesPerformance/seller/export", method = RequestMethod.GET)
    @ResponseBody
    public HttpEntity<byte[]> exportSellerSalesPerformanceList(SalesPerformanceRequest request, @CurrentAdminUser AdminUser operator) throws Exception {
        return salesPerformanceExportFacade.exportSellerSalesPerformance(request, operator);
    }

    @RequestMapping(value = "api/salesPerformance/restaurant/dailyCount", method = RequestMethod.GET)
    @ResponseBody
    public void dailyCountRestaurantSalesPerformance(SalesPerformanceRequest request) {
        restaurantSalesPerformanceFacade.dailyCountRestaurantSalesPerformance(request);
    }

    @RequestMapping(value = "api/salesPerformance/restaurant/list", method = RequestMethod.GET)
    @ResponseBody
    public QueryResponse<RestaurantSalesPerformanceWrapper> getRestaurantSalesPerformanceWrapperList(SalesPerformanceRequest request) {
        return restaurantSalesPerformanceFacade.getRestaurantSalesPerformanceList(request);
    }

    @RequestMapping(value = "api/salesPerformance/restaurant/export", method = RequestMethod.GET)
    @ResponseBody
    public HttpEntity<byte[]> exportRestaurantSalesPerformanceList(SalesPerformanceRequest request, @CurrentAdminUser AdminUser operator) throws Exception {
        return salesPerformanceExportFacade.exportRestaurantSalesPerformance(request, operator);
    }

    @RequestMapping(value = "api/salesPerformance/block/dailyCount", method = RequestMethod.GET)
    @ResponseBody
    public void dailyCountBlockSalesPerformance(SalesPerformanceRequest request) {
        blockSalesPerformanceFacade.dailyCountBlockSalesPerformance(request);
    }

    @RequestMapping(value = "api/salesPerformance/block/list", method = RequestMethod.GET)
    @ResponseBody
    public QueryResponse<BlockSalesPerformanceWrapper> getBlockSalesPerformanceWrapperList(SalesPerformanceRequest request) {
        return blockSalesPerformanceFacade.getBlockSalesPerformanceList(request);
    }

    @RequestMapping(value = "api/salesPerformance/block/export", method = RequestMethod.GET)
    @ResponseBody
    public HttpEntity<byte[]> exportBlockSalesPerformanceList(SalesPerformanceRequest request, @CurrentAdminUser AdminUser operator) throws Exception {
        return salesPerformanceExportFacade.exportBlockSalesPerformance(request, operator);
    }
}
