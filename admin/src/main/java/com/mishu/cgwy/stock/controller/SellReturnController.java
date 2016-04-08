package com.mishu.cgwy.stock.controller;

import com.mishu.cgwy.admin.controller.CurrentAdminUser;
import com.mishu.cgwy.admin.domain.AdminUser;
import com.mishu.cgwy.order.controller.SellReturnRequest;
import com.mishu.cgwy.order.facade.OrderFacade;
import com.mishu.cgwy.order.wrapper.OrderWrapper;
import com.mishu.cgwy.response.query.QueryResponse;
import com.mishu.cgwy.stock.domain.SellReturnStatus;
import com.mishu.cgwy.stock.domain.SellReturnType;
import com.mishu.cgwy.stock.dto.SellReturnQueryRequest;
import com.mishu.cgwy.stock.facade.SellReturnFacade;
import com.mishu.cgwy.stock.wrapper.SellReturnReasonWrapper;
import com.mishu.cgwy.stock.wrapper.SellReturnWrapper;
import com.mishu.cgwy.stock.wrapper.SimpleSellReturnItemWrapper;
import com.mishu.cgwy.stock.wrapper.SimpleSellReturnWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.http.HttpEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Created by wangwei on 15/10/12.
 */
@Controller
public class SellReturnController {

    @Autowired
    private SellReturnFacade sellReturnFacade;

    @Autowired
    private OrderFacade orderFacade;

    @InitBinder
    public void initBinder(WebDataBinder binder) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        dateFormat.setLenient(false);
        binder.registerCustomEditor(Date.class, new CustomDateEditor(dateFormat, false));
    }

    @RequestMapping(value = "/api/sellReturn/reasons", method = RequestMethod.GET)
    @ResponseBody
    public List<SellReturnReasonWrapper> getSellReturnReasons() {
        return sellReturnFacade.getSellReturnReasons();
    }

    @RequestMapping(value = "/api/sellReturn/status", method = RequestMethod.GET)
    @ResponseBody
    public SellReturnStatus[] getSellReturnStatus() {
        return SellReturnStatus.values();
    }

    @RequestMapping(value = "/api/sellReturn/type/list", method = RequestMethod.GET)
    @ResponseBody
    public SellReturnType[] getSellReturnTypeList() {
        return SellReturnType.values();
    }

    @RequestMapping(value = "/api/sellReturn", method = RequestMethod.POST)
    @ResponseBody
    public OrderWrapper orderSellReturn(@RequestBody SellReturnRequest request, @CurrentAdminUser AdminUser adminUser) {
        sellReturnFacade.createOrderSellReturn(request, adminUser);
        return orderFacade.adminGetOrderById(request.getOrderId());
    }

    @RequestMapping(value = "/api/sellReturn", method = RequestMethod.GET)
    @ResponseBody
    public QueryResponse<SimpleSellReturnWrapper> getSellReturns(SellReturnQueryRequest request, @CurrentAdminUser AdminUser operator) {
        return sellReturnFacade.getSellReturn(request, operator);
    }

    @RequestMapping(value = "/api/sellReturnItem", method = RequestMethod.GET)
    @ResponseBody
    public QueryResponse<SimpleSellReturnItemWrapper> getSellReturnItems(SellReturnQueryRequest request, @CurrentAdminUser AdminUser operator) {
        return sellReturnFacade.getSellReturnItem(request, operator);
    }

    @RequestMapping(value = "/api/sellReturn/{id}", method = RequestMethod.GET)
    @ResponseBody
    public SellReturnWrapper getSellReturn(@PathVariable("id") Long id) {
        return sellReturnFacade.getSellReturn(id);
    }

    @RequestMapping(value = "/api/sellReturn/{id}", method = RequestMethod.PUT)
    @ResponseBody
    public void updateSellReturn(@PathVariable("id") Long id, @RequestParam("status") Integer status,
                                 @RequestParam(value = "auditOpinion", required = false) String auditOpinion, @CurrentAdminUser AdminUser operator) {
        sellReturnFacade.updateSellReturn(id, status, auditOpinion, operator);
    }

    @RequestMapping(value = "/api/sellReturn/export/list", method = RequestMethod.GET)
    @ResponseBody
    public HttpEntity<byte[]> exportSellReturn(SellReturnQueryRequest request, @CurrentAdminUser AdminUser operator) throws Exception{
        return sellReturnFacade.exportSellReturnList(request, operator);
    }

    @RequestMapping(value = "/api/sellReturnItem/export/list", method = RequestMethod.GET)
    @ResponseBody
    public HttpEntity<byte[]> exportSellReturnItem(SellReturnQueryRequest request, @CurrentAdminUser AdminUser operator) throws Exception{
        return sellReturnFacade.exportSellReturnItemList(request, operator);
    }
}
