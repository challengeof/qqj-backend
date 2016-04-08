package com.mishu.cgwy.accounting.controller;

import com.mishu.cgwy.accounting.dto.ProfitRequest;
import com.mishu.cgwy.accounting.facade.ProfitFacade;
import com.mishu.cgwy.accounting.wrapper.*;
import com.mishu.cgwy.admin.controller.CurrentAdminUser;
import com.mishu.cgwy.admin.domain.AdminUser;
import com.mishu.cgwy.response.query.QueryResponse;
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
public class ProfitController {

    @Autowired
    private ProfitFacade profitFacade;

    @InitBinder
    public void initBinder(WebDataBinder binder) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        dateFormat.setLenient(false);
        binder.registerCustomEditor(Date.class, new CustomDateEditor(dateFormat, false));
    }

    @RequestMapping(value = "api/profit/skuSellSummeryProfit/export", method = RequestMethod.GET)
    @ResponseBody
    public HttpEntity<byte[]> exportSkuSellSummeryProfit(ProfitRequest request) throws Exception {
        return profitFacade.exportSkuSellSummeryProfit(request);
    }


    @RequestMapping(value = "api/profit/skuSellSummeryProfit/list", method = RequestMethod.GET)
    @ResponseBody
    public QueryResponse<SkuSellSummeryProfitWrapper> getSkuSellSummeryProfit(ProfitRequest request) {
        return profitFacade.getSkuSellSummeryProfit(request);
    }

    @RequestMapping(value = "api/profit/customerSkuProfit/list", method = RequestMethod.GET)
    @ResponseBody
    public QueryResponse<CustomerSkuProfitWrapper> getCustomerProfitWrapper(ProfitRequest request) {
        return profitFacade.getCustomerSkuProfit(request);
    }

    @RequestMapping(value = "api/profit/customerSkuProfit/export", method = RequestMethod.GET)
    @ResponseBody
    public HttpEntity<byte[]> exportCustomerGrossProfitList(ProfitRequest request) throws Exception {
        return profitFacade.exportCustomerSkuProfitList(request);
    }

    @RequestMapping(value = "api/profit/warehouseCategoryProfit/list", method = RequestMethod.GET)
    @ResponseBody
    public WarehouseCategoryProfitArrays getWarehouseCategoryProfit(ProfitRequest request) {
        return profitFacade.getWarehouseCategoryProfit(request);
    }

    @RequestMapping(value = "api/profit/categorySellerProfit/list", method = RequestMethod.GET)
    @ResponseBody
    public CategorySellerProfitArrays getCategorySellerProfit(ProfitRequest request) {
        return profitFacade.getCategorySellerProfit(request);
    }

    @RequestMapping(value = "api/profit/skuProfit/list", method = RequestMethod.GET)
    @ResponseBody
    public QueryResponse<SkuProfitWrapper> getSkuProfitList(ProfitRequest request) {
        return profitFacade.getSkuProfitList(request);
    }

    @RequestMapping(value = "api/profit/skuProfit/export", method = RequestMethod.GET)
    @ResponseBody
    public HttpEntity<byte[]> exportSkuProfitList(ProfitRequest request) throws Exception {
        return profitFacade.exportSkuProfitList(request);
    }

    @RequestMapping(value = "api/profit/customerSellerProfit/list", method = RequestMethod.GET)
    @ResponseBody
    public CustomerSellerProfitArrays getCustomerSellerProfitList(ProfitRequest request) {
        return profitFacade.getCustomerSellerProfit(request);
    }

    @RequestMapping(value = "api/profit/customerSellerProfit/export", method = RequestMethod.GET)
    @ResponseBody
    public void exportCustomerSellerProfitList(ProfitRequest request, @CurrentAdminUser AdminUser adminUser) throws Exception {
        profitFacade.asyncExportCustomerSellerProfit(request, adminUser);
    }

    @RequestMapping(value = "api/profit/categorySellerProfit/export", method = RequestMethod.GET)
    @ResponseBody
    public void exportCategorySellerProfitList(ProfitRequest request, @CurrentAdminUser AdminUser adminUser) throws Exception {
        profitFacade.asyncExportCategorySellerProfit(request, adminUser);
    }

    @RequestMapping(value = "api/profit/skuSales/list", method = RequestMethod.GET)
    @ResponseBody
    public QueryResponse<SkuSalesWrapper> getSkuSalesList(ProfitRequest request) {
        return profitFacade.getSkuSalesList(request);
    }

    @RequestMapping(value = "api/profit/skuSales/export", method = RequestMethod.GET)
    @ResponseBody
    public HttpEntity<byte[]> exportSkuSalesList(ProfitRequest request) throws Exception {
        return profitFacade.exportSkuSalesList(request);
    }
}
