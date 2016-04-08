package com.mishu.cgwy.crm.saleVisit.controller;

import com.mishu.cgwy.admin.controller.CurrentAdminUser;
import com.mishu.cgwy.admin.domain.AdminUser;
import com.mishu.cgwy.profile.constants.RestaurantActiveType;
import com.mishu.cgwy.response.query.QueryResponse;
import com.mishu.cgwy.saleVisit.constants.*;
import com.mishu.cgwy.saleVisit.facade.SaleVisitFacade;
import com.mishu.cgwy.saleVisit.request.SaleVisitPostData;
import com.mishu.cgwy.saleVisit.request.SaleVisitQueryRequest;
import com.mishu.cgwy.saleVisit.vo.SaleVisitVo;
import com.mishu.cgwy.saleVisit.wrapper.SaleVisitWrapper;
import com.mishu.cgwy.utils.UserDateEditor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;

import java.util.Date;

/**
 * Created by xiao1zhao2 on 16/3/11.
 */
@Controller
public class SaleVisitController {

    @Autowired
    private SaleVisitFacade saleVisitFacade;

    @InitBinder
    public void initBinder(WebDataBinder binder) {
        binder.registerCustomEditor(Date.class, new UserDateEditor());
    }

    @RequestMapping(value = "/api/saleVisit/update", method = RequestMethod.POST)
    @ResponseBody
    public SaleVisitWrapper updateSaleVisit(@RequestBody SaleVisitPostData data, @CurrentAdminUser AdminUser operator) {
        return saleVisitFacade.updateSaleVisit(data, operator);
    }

    @RequestMapping(value = "/api/saleVisit/{id}", method = RequestMethod.DELETE)
    @ResponseBody
    public void deleteSaleVisit(@PathVariable(value = "id") Long id) {
        saleVisitFacade.deleteSaleVisit(id);
    }


    /*
     暂时注释
    @RequestMapping(value = "/api/saleVisit/{id}", method = RequestMethod.GET)
    @ResponseBody
    public SaleVisitWrapper getSaleVisitById(@PathParam(value = "id") Long id) {
        return saleVisitFacade.getSaleVisitById(id);
    }*/

    @RequestMapping(value = "/api/saleVisit/{id}", method = RequestMethod.GET)
    @ResponseBody
    public SaleVisitVo getSaleVisitById(@PathVariable(value = "id") Long id) {
        return saleVisitFacade.getSaleVisitById(id);
    }


    @RequestMapping(value = "/api/saleVisit/list", method = RequestMethod.GET)
    @ResponseBody
    public QueryResponse<SaleVisitWrapper> getSaleVisitPage(SaleVisitQueryRequest request) {
        return saleVisitFacade.getSaleVisitPage(request);
    }

    @RequestMapping(value = "/api/saleVisit/export/excel",method = RequestMethod.GET)
    @ResponseBody
    public HttpEntity<byte[]> generateSaleVisitExcel
            (SaleVisitQueryRequest request, @CurrentAdminUser AdminUser operator)throws Exception
    {

        return saleVisitFacade.generateSaleVisitExcel(request,operator);
    }


    @RequestMapping(value = "/api/saleVisit/purpose/list", method = RequestMethod.GET)
    @ResponseBody
    public SaleVisitPurpose[] getSaleVisitPurposeList() {
        return SaleVisitPurpose.values();
    }

    @RequestMapping(value = "/api/saleVisit/stage/list", method = RequestMethod.GET)
    @ResponseBody
    public SaleVisitStage[] getSaleVisitStageList() {
        return SaleVisitStage.values();
    }

    @RequestMapping(value = "/api/saleVisit/intentionProduction/list", method = RequestMethod.GET)
    @ResponseBody
    public SaleVisitIntentionProduction[] getSaleVisitIntentionProductionList() {
        return SaleVisitIntentionProduction.values();
    }

    @RequestMapping(value = "/api/saleVisit/trouble/list", method = RequestMethod.GET)
    @ResponseBody
    public SaleVisitTrouble[] getSaleVisitTroubleList() {
        return SaleVisitTrouble.values();
    }

    @RequestMapping(value = "/api/saleVisit/solution/list", method = RequestMethod.GET)
    @ResponseBody
    public SaleVisitSolution[] getSaleVisitSolutionList() {
        return SaleVisitSolution.values();
    }

    @RequestMapping(value = "/api/saleVisit/activeType/list", method = RequestMethod.GET)
    @ResponseBody
    public RestaurantActiveType[] getCustomerActiveTypeList(){return RestaurantActiveType.values();}


}
