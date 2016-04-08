package com.mishu.cgwy.stock.controller;

import com.mishu.cgwy.admin.controller.CurrentAdminUser;
import com.mishu.cgwy.admin.domain.AdminUser;
import com.mishu.cgwy.response.query.QueryResponse;
import com.mishu.cgwy.response.query.QuerySummationResponse;
import com.mishu.cgwy.stock.domain.SellReturnType;
import com.mishu.cgwy.stock.domain.StockInStatus;
import com.mishu.cgwy.stock.domain.StockInType;
import com.mishu.cgwy.stock.dto.StockInData;
import com.mishu.cgwy.stock.dto.StockInRequest;
import com.mishu.cgwy.stock.facade.StockInFacade;
import com.mishu.cgwy.stock.wrapper.StockInItemWrapper;
import com.mishu.cgwy.stock.wrapper.StockInWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Admin on 16/9/15.
 */
@Controller
public class StockInController {

    @Autowired
    private StockInFacade stockInFacade;

    @InitBinder
    public void initBinder(WebDataBinder binder) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        dateFormat.setLenient(false);
        binder.registerCustomEditor(Date.class, new CustomDateEditor(dateFormat, false));
    }

    @RequestMapping(value = "/api/stockIn/query", method = RequestMethod.GET)
    @ResponseBody
    public QueryResponse<StockInWrapper> stockInQuery(StockInRequest stockInRequest, @CurrentAdminUser AdminUser operator) {
        return stockInFacade.getStockInList(stockInRequest, operator);
    }

    @RequestMapping(value = "/api/stockIn/{id}", method = RequestMethod.GET)
    @ResponseBody
    public StockInWrapper getStockInDetail(@PathVariable(value = "id") Long id) {
        return stockInFacade.getStockIn(id);
    }

    @RequestMapping(value = "/api/stockInItem/query", method = RequestMethod.GET)
    @ResponseBody
    public QuerySummationResponse<StockInItemWrapper> stockInItemQuery(StockInRequest stockInRequest, @CurrentAdminUser AdminUser operator) {
        return stockInFacade.getStockInItemList(stockInRequest, operator);
    }

    @RequestMapping(value = "/api/stockIn/receive/{stockInId}", method = RequestMethod.GET)
    @ResponseBody
    public StockInWrapper getStockIn(@PathVariable(value = "stockInId") Long stockInId) {
        return stockInFacade.getStockIn(stockInId);
    }

    @RequestMapping(value = "/api/stockIn/receive/add", method = RequestMethod.POST)
    @ResponseBody
    public void saveStockIn(@RequestBody StockInData stockInData, @CurrentAdminUser AdminUser adminUser) {
        stockInFacade.completeStockIn(stockInData, adminUser);
    }

    @RequestMapping(value = "/api/stockIn/type/list", method = RequestMethod.GET)
    @ResponseBody
    public StockInType[] listStockInType() {
        return StockInType.values();
    }

    @RequestMapping(value = "/api/stockIn/status/list", method = RequestMethod.GET)
    @ResponseBody
    public StockInStatus[] listStockInStatus() {
        return StockInStatus.values();
    }

    @RequestMapping(value = "/api/stockIn/sellReturnType/list", method = RequestMethod.GET)
    @ResponseBody
    public SellReturnType[] listSellReturnType() {
        return SellReturnType.values();
    }

}
