package com.mishu.cgwy.stock.controller;

import com.mishu.cgwy.admin.controller.CurrentAdminUser;
import com.mishu.cgwy.admin.domain.AdminUser;
import com.mishu.cgwy.response.query.QueryResponse;
import com.mishu.cgwy.response.query.QuerySummationResponse;
import com.mishu.cgwy.stock.domain.StockOut;
import com.mishu.cgwy.stock.domain.StockOutStatus;
import com.mishu.cgwy.stock.domain.StockOutType;
import com.mishu.cgwy.stock.dto.StockOutData;
import com.mishu.cgwy.stock.dto.StockOutRequest;
import com.mishu.cgwy.stock.facade.StockOutFacade;
import com.mishu.cgwy.stock.wrapper.StockOutItemWrapper;
import com.mishu.cgwy.stock.wrapper.StockOutWrapper;
import com.mishu.cgwy.utils.UserDateEditor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;
import java.util.Set;

/**
 * Created by xiao1zhao2 on 15/9/16.
 */
@Controller
public class StockOutController {

    @Autowired
    private StockOutFacade stockOutFacade;

    @InitBinder
    public void initBinder(WebDataBinder binder) {
        binder.registerCustomEditor(Date.class, new UserDateEditor());
    }

    @RequestMapping(value = "/api/stockOut/query", method = RequestMethod.GET)
    @ResponseBody
    public QuerySummationResponse<StockOutWrapper> stockOutQuery(StockOutRequest stockOutRequest, @CurrentAdminUser AdminUser operator) {
        return stockOutFacade.getStockOutList(stockOutRequest, operator);
    }

    @RequestMapping(value = "/api/stockOut/{id}", method = RequestMethod.GET)
    @ResponseBody
    public StockOutWrapper getStockOutDetail(@PathVariable(value = "id") Long id) {
        return stockOutFacade.getStockOut(id);
    }

    @RequestMapping(value = "/api/stockOutItem/query", method = RequestMethod.GET)
    @ResponseBody
    public QueryResponse<StockOutItemWrapper> stockOutItemQuery(StockOutRequest stockOutRequest, @CurrentAdminUser AdminUser operator) {
        return stockOutFacade.getStockOutItemList(stockOutRequest, operator);
    }

    @RequestMapping(value = "/api/stockOut/send/{stockOutId}", method = RequestMethod.GET)
    @ResponseBody
    public StockOutWrapper getDistributedStockOut(@PathVariable(value = "stockOutId") Long stockOutId) {
        return stockOutFacade.getDistributedStockOut(stockOutId);
    }

    @RequestMapping(value = "/api/stockOut/send/add", method = RequestMethod.POST)
    @ResponseBody
    public void stockOutConfirmOut(@RequestBody StockOutData stockOutData, @CurrentAdminUser AdminUser adminUser) {
        stockOutFacade.stockOutConfirmOut(stockOutData, adminUser);
    }

    @RequestMapping(value = "/api/stockOut/send/add-all", method = RequestMethod.POST)
    @ResponseBody
    public void stockOutConfirmOutAll(@RequestBody StockOutData stockOutData, @CurrentAdminUser AdminUser adminUser) {
        stockOutFacade.stockOutConfirmOutAll(stockOutData, adminUser);
    }

    @RequestMapping(value = "/api/stockOut/send/finish", method = RequestMethod.POST)
    @ResponseBody
    public void stockOutEnd(@RequestBody StockOutData stockOutData, @CurrentAdminUser AdminUser adminUser) {
        StockOut stockOut = stockOutFacade.stockOutFinish(stockOutData, adminUser);
        //从事务里抽出来 保证收货结束（事务结束）后才去发消息做后续操作
        stockOutFacade.sendCouponAndScoreMessage(stockOutData,stockOut);
    }

    @RequestMapping(value = "/api/stockOut/send/finish-all", method = RequestMethod.POST)
    @ResponseBody
    public void stockOutEndAll(@RequestBody StockOutData stockOutData, @CurrentAdminUser AdminUser adminUser) {
        List<StockOut> stockOuts = stockOutFacade.stockOutFinishAll(stockOutData, adminUser);

        //从事务里抽出来 保证收货结束（事务结束）后才去发消息
        if(stockOuts!=null) {
            stockOutFacade.sendCouponAndScoreMessage(stockOutData, stockOuts.toArray(new StockOut[]{}));
        }
    }

    @RequestMapping(value = "/api/stockOut/send/before-add-all", method = RequestMethod.POST)
    @ResponseBody
    public Set<String> beforeConfirmOutAll(@RequestBody StockOutData stockOutData) {
        return stockOutFacade.beforeConfirmOutAll(stockOutData);
    }

    @RequestMapping(value = "/api/stockOut/type/list", method = RequestMethod.GET)
    @ResponseBody
    public StockOutType[] getStockOutType() {
        return StockOutType.values();
    }

    @RequestMapping(value = "/api/stockOut/status/list", method = RequestMethod.GET)
    @ResponseBody
    public StockOutStatus[] getStockOutStatus() {
        return StockOutStatus.values();
    }

}
