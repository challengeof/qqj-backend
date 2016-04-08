package com.mishu.cgwy.stock.controller;

import com.mishu.cgwy.admin.controller.CurrentAdminUser;
import com.mishu.cgwy.admin.domain.AdminUser;
import com.mishu.cgwy.order.controller.SellCancelRequest;
import com.mishu.cgwy.order.facade.OrderFacade;
import com.mishu.cgwy.order.wrapper.OrderWrapper;
import com.mishu.cgwy.order.wrapper.PromotionWrapper;
import com.mishu.cgwy.response.query.QueryResponse;
import com.mishu.cgwy.stock.domain.SellCancelType;
import com.mishu.cgwy.stock.dto.SellCancelCheckPromotionResponse;
import com.mishu.cgwy.stock.dto.SellCancelQueryRequest;
import com.mishu.cgwy.stock.facade.SellCancelFacade;
import com.mishu.cgwy.stock.wrapper.SellCancelWrapper;
import com.mishu.cgwy.stock.wrapper.SimpleSellCancelItemWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.http.HttpEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import scala.tools.nsc.doc.model.Public;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Created by wangwei on 15/10/15.
 */
@Controller
public class SellCancelController {

    @Autowired
    private SellCancelFacade sellCancelFacade;
    @Autowired
    private OrderFacade orderFacade;

    @InitBinder
    public void initBinder(WebDataBinder binder) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        dateFormat.setLenient(false);
        binder.registerCustomEditor(Date.class, new CustomDateEditor(dateFormat, false));
    }

    @RequestMapping(value = "/api/sellCancel", method = RequestMethod.POST)
    @ResponseBody
    public OrderWrapper createSellCancel(@RequestBody SellCancelRequest request, @CurrentAdminUser AdminUser adminUser) {
        sellCancelFacade.createSellCancel(request, adminUser);
        return orderFacade.adminGetOrderById(request.getOrderId());
    }


    @RequestMapping(value = "/api/order/newPromotion", method = RequestMethod.POST)
    @ResponseBody
    public SellCancelCheckPromotionResponse checkOrderNewPromotion(@RequestBody SellCancelRequest request) {
        return sellCancelFacade.checkSellCancelPromotionAndCoupon(request);
    }

    @RequestMapping(value = "/api/sellCancel/list", method = RequestMethod.GET)
    @ResponseBody
    public QueryResponse<SellCancelWrapper> getSellCancelList(SellCancelQueryRequest request, @CurrentAdminUser AdminUser operator) {
        return sellCancelFacade.getSellCancelList(request, operator);
    }

    @RequestMapping(value = "/api/sellCancel/{id}", method = RequestMethod.GET)
    @ResponseBody
    public SellCancelWrapper getSellCancelById(@PathVariable(value = "id")Long id) {
        return sellCancelFacade.getSellCancelById(id);
    }

    @RequestMapping(value = "/api/sellCancelItem/list", method = RequestMethod.GET)
    @ResponseBody
    public QueryResponse<SimpleSellCancelItemWrapper> getSellCancelItemList(SellCancelQueryRequest request, @CurrentAdminUser AdminUser operator) {
        return sellCancelFacade.getSellCancelItemList(request, operator);
    }

    @RequestMapping(value = "/api/sellCancel/type/list", method = RequestMethod.GET)
    @ResponseBody
    public SellCancelType[] getSellCancelTypeList() {
        return SellCancelType.values();
    }


    @RequestMapping(value = "/api/sellCancel/export/list", method = RequestMethod.GET)
    @ResponseBody
    public HttpEntity<byte[]> exportSellCancel(SellCancelQueryRequest request, @CurrentAdminUser AdminUser operator) throws Exception {
        return sellCancelFacade.exportSellCancelList(request, operator);
    }

    @RequestMapping(value = "/api/sellCancelItem/export/list", method = RequestMethod.GET)
    @ResponseBody
    public HttpEntity<byte[]> exportSellCancelItem(SellCancelQueryRequest request, @CurrentAdminUser AdminUser operator) throws Exception {
        return sellCancelFacade.exportSellCancelItemList(request, operator);
    }
}
