package com.mishu.cgwy.console.controller;

import com.mishu.cgwy.order.service.OrderService;
import com.mishu.cgwy.product.facade.ProductFacade;
import com.mishu.cgwy.product.service.SkuSalesStatisticsService;
import com.mishu.cgwy.search.SearchService;
import org.apache.commons.lang.time.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * User: xudong
 * Date: 5/3/15
 * Time: 9:15 PM
 */
@Controller
public class ConsoleController {
    @Autowired
    private SearchService searchService;

    @Autowired
    private SkuSalesStatisticsService skuSalesStatisticsService;

    @Autowired
    private ProductFacade productFacade;

    @Autowired
    private OrderService orderService;

    @InitBinder
    public void initBinder(WebDataBinder binder) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        dateFormat.setLenient(false);
        binder.registerCustomEditor(Date.class, new CustomDateEditor(dateFormat, false));
    }

    @RequestMapping(value = "/api/search/rebuild")
    @ResponseBody
    public String rebuildSearchIndex() throws IOException {
        searchService.rebuildIndex();
        return "success";
    }

    @RequestMapping(value = "/api/search/createSkuPriceHistoryIndex")
    @ResponseBody
    public String createSkuPriceHistoryIndex() throws Exception {
        searchService.createSkuPriceHistoryIndex();
        return "success";
    }

    @RequestMapping(value = "/api/search/deleteSkuPriceHistoryIndex")
    @ResponseBody
    public String deleteSkuPriceHistoryIndex() throws Exception {
        searchService.deleteSkuPriceHistoryIndex();
        return "success";
    }

    @RequestMapping(value = "/api/search/buildSkuPriceHistoryIndex")
    @ResponseBody
    public String buildSkuPriceHistoryIndex() throws Exception {
        searchService.buildSkuPriceHistoryIndex();
        return "success";
    }

    @RequestMapping(value = "/api/statistics/rebuild")
    @ResponseBody
    public String rebuildStatistics() throws IOException {
        skuSalesStatisticsService.refreshSkuSalesStatistics();
        return "success";
    }

    @RequestMapping(value = "/api/console/deliver-order")
    @ResponseBody
    public String updateOrder(@RequestParam("date") Date date) throws IOException {
        date = DateUtils.truncate(date, Calendar.DAY_OF_MONTH);
        orderService.deliverOrder(DateUtils.addDays(date, 1));
        return "success";
    }

    @RequestMapping(value = "/api/console/warehouse/sync-sku")
    @ResponseBody
    public String syncSkuToWarehouse(@RequestParam("warehouseIds") Long[] warehouseIds) throws IOException {
        if (warehouseIds != null) {
            for (Long warehouseId : warehouseIds) {
                productFacade.syncAllSkuToWarehouse(warehouseId);
            }
        }
        return "success";
    }
}
