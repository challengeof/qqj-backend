package com.mishu.cgwy.stock.controller;

import com.mishu.cgwy.admin.controller.CurrentAdminUser;
import com.mishu.cgwy.admin.domain.AdminUser;
import com.mishu.cgwy.organization.vo.OrganizationVo;
import com.mishu.cgwy.product.wrapper.CandidateSkuWrapper;
import com.mishu.cgwy.response.query.QueryResponse;
import com.mishu.cgwy.stock.domain.StockAdjustStatus;
import com.mishu.cgwy.stock.dto.StockAdjustData;
import com.mishu.cgwy.stock.dto.StockAdjustQueryRequest;
import com.mishu.cgwy.stock.facade.StockAdjustFacade;
import com.mishu.cgwy.stock.wrapper.StockAdjustWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;

import java.text.SimpleDateFormat;
import java.util.Date;

@Controller
public class StockAdjustController {

    @Autowired
    private StockAdjustFacade stockAdjustFacade;

    @InitBinder
    public void initBinder(WebDataBinder binder) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        dateFormat.setLenient(false);
        binder.registerCustomEditor(Date.class, new CustomDateEditor(dateFormat, false));
    }

    @RequestMapping(value = "/api/stockAdjust/adjust", method = RequestMethod.POST)
    @ResponseBody
    public void adjustStock(@RequestBody StockAdjustData stockAdjustData, @CurrentAdminUser AdminUser adminUser) {
        stockAdjustFacade.adjustStock(stockAdjustData, adminUser);
    }

    @RequestMapping(value = "/api/stockAdjust/createAdjust", method = RequestMethod.POST)
    @ResponseBody
    public void createAdjust(@RequestBody StockAdjustData stockAdjustData, @CurrentAdminUser AdminUser adminUser) {
        stockAdjustFacade.createAdjust(stockAdjustData, adminUser);
    }

    @RequestMapping(value = "/api/stockAdjust/query", method = RequestMethod.GET)
    @ResponseBody
    public QueryResponse<StockAdjustWrapper> stockAdjustQuery(StockAdjustQueryRequest request, @CurrentAdminUser AdminUser operator) {
        return stockAdjustFacade.getStockAdjustList(request, operator);
    }

    @RequestMapping(value = "/api/stockAdjust/confirm", method = RequestMethod.POST)
    @ResponseBody
    public void adjustConfirm(@RequestBody StockAdjustData stockAdjustData, @CurrentAdminUser AdminUser adminUser) {
        stockAdjustFacade.adjustConfirm(stockAdjustData, adminUser);
    }

    @RequestMapping(value = "/api/stockAdjust/reject", method = RequestMethod.POST)
    @ResponseBody
    public void adjustReject(@RequestBody StockAdjustData stockAdjustData, @CurrentAdminUser AdminUser adminUser) {
        stockAdjustFacade.adjustReject(stockAdjustData, adminUser);
    }

    @RequestMapping(value = "/api/stockAdjust/cancel", method = RequestMethod.POST)
    @ResponseBody
    public void adjustCancel(@RequestBody StockAdjustData stockAdjustData, @CurrentAdminUser AdminUser adminUser) {
        stockAdjustFacade.adjustCancel(stockAdjustData, adminUser);
    }

    @RequestMapping(value = "/api/stockAdjust/defaultOrganization", method = RequestMethod.GET)
    @ResponseBody
    public OrganizationVo getDefaultOrganization() {
        return stockAdjustFacade.getDefaultOrganization();
    }

    @RequestMapping(value = "/api/stockAdjust/sku/{id}", method = RequestMethod.GET)
    @ResponseBody
    public CandidateSkuWrapper getAdjustSku(@PathVariable("id") Long id) {
        return stockAdjustFacade.getSku(id);
    }

    @RequestMapping(value = "/api/stockAdjust/status/list", method = RequestMethod.GET)
    @ResponseBody
    public StockAdjustStatus[] getStockOutStatus() {
        return StockAdjustStatus.values();
    }

}
