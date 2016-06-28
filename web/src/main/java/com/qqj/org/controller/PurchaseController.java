package com.qqj.org.controller;

import com.qqj.org.domain.Customer;
import com.qqj.org.facade.OrgFacade;
import com.qqj.purchase.controller.AuditPurchaseRequest;
import com.qqj.purchase.controller.PurchaseRequest;
import com.qqj.purchase.enumeration.PurchaseAuditStatus;
import com.qqj.purchase.facade.PurchaseFacade;
import com.qqj.response.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Created by wangguodong on 16/6/24.
 */
@Controller
public class PurchaseController {

    @Autowired
    private PurchaseFacade purchaseFacade;

    @Autowired
    private OrgFacade orgFacade;

    @RequestMapping(value = "/api/purchase", method = RequestMethod.POST)
    @ResponseBody
    public Response purchase(@CurrentCustomer Customer parent, @RequestBody PurchaseRequest purchaseRequest) {
        return purchaseFacade.purchase(parent, purchaseRequest);
    }

    //代理审批
    @RequestMapping(value = "/api/purchase/audit", method = RequestMethod.POST)
    @ResponseBody
    public Response auditPurchase(@CurrentCustomer Customer customer, @RequestBody AuditPurchaseRequest request) {
        return purchaseFacade.auditPurchase(request);
    }

    @RequestMapping(value = "/api/purchase/status-enumeration", method = RequestMethod.GET)
    @ResponseBody
    public PurchaseAuditStatus[] getPurchaseStatusEnumeration() {
        return PurchaseAuditStatus.values();
    }
}
