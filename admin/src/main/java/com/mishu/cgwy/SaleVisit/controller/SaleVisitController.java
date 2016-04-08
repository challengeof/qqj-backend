//package com.mishu.cgwy.saleVisit.controller;
//
//import com.mishu.cgwy.admin.controller.CurrentAdminUser;
//import com.mishu.cgwy.admin.domain.AdminUser;
//import com.mishu.cgwy.saleVisit.constants.HaveNoOrderContinueStatus;
//import com.mishu.cgwy.saleVisit.constants.HaveNoOrderNotContinueStatus;
//import com.mishu.cgwy.saleVisit.constants.SaleVisitStatus;
//import com.mishu.cgwy.saleVisit.controller.SaleVisitQueryRequest;
//import com.mishu.cgwy.saleVisit.controller.SaleVisitQueryResponse;
//import com.mishu.cgwy.saleVisit.controller.SaleVisitRequest;
//import com.mishu.cgwy.saleVisit.facade.SaleVisitFacade;
//import com.mishu.cgwy.saleVisit.wrapper.SaleVisitWrapper;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.beans.propertyeditors.CustomDateEditor;
//import org.springframework.stereotype.Controller;
//import org.springframework.web.bind.WebDataBinder;
//import org.springframework.web.bind.annotation.*;
//
//import java.text.SimpleDateFormat;
//import java.util.ArrayList;
//import java.util.Date;
//import java.util.List;
//
///**
// * Created by apple on 15/8/13.
// */
//@Controller
//public class SaleVisitController {
//
//    @Autowired
//    private SaleVisitFacade saleVisitFacade;
//
//    @InitBinder
//    public void initBinder(WebDataBinder binder) {
//        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
//        dateFormat.setLenient(false);
//        binder.registerCustomEditor(Date.class, new CustomDateEditor(dateFormat, false));
//    }
//
//    @RequestMapping(value = "/api/saleVisit", method = RequestMethod.POST)
//    @ResponseBody
//    public SaleVisitWrapper createSaleVisit(@RequestBody SaleVisitRequest request, @CurrentAdminUser AdminUser adminUser) {
//        return saleVisitFacade.createSaleVisit(request, adminUser);
//    }
//
//    @RequestMapping(value = "/api/saleVisit", method = RequestMethod.GET)
//    @ResponseBody
//    public SaleVisitQueryResponse getSaleVist(SaleVisitQueryRequest request, @CurrentAdminUser AdminUser adminUser) {
//        return saleVisitFacade.getAll(request, adminUser);
//    }
//
//    @RequestMapping(value = "/api/saleVisit/status", method = RequestMethod.GET)
//    @ResponseBody
//    public SaleVisitStatus[] getSaleVisitStatus() {
//        return SaleVisitStatus.values();
//    }
//
//    @RequestMapping(value = "/api/saleVisit/status/{id}/reason", method = RequestMethod.GET)
//    @ResponseBody
//    public List<SaleVisitReasonResponse> getSaleVisitReason(@PathVariable("id") Integer status) {
//        SaleVisitStatus saleVisitStatus = SaleVisitStatus.fromInt(status);
//        List<SaleVisitReasonResponse> list = new ArrayList<>();
//        if (saleVisitStatus.getValue().equals(SaleVisitStatus.HAVE_NOORDER_CONTINUE.getValue())) {
//            for (HaveNoOrderContinueStatus status1 : HaveNoOrderContinueStatus.values()) {
//                list.add(new SaleVisitReasonResponse(status1));
//            }
//            return list;
//        } else if (saleVisitStatus.getValue().equals(SaleVisitStatus.HAVA_NOORDER_NOTCONTINUE.getValue())) {
//            for (HaveNoOrderNotContinueStatus status1 : HaveNoOrderNotContinueStatus.values()) {
//                list.add(new SaleVisitReasonResponse(status1));
//            }
//            return list;
//        } else {
//            return null;
//        }
//
//    }
//}
