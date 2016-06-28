package com.qqj.org.controller;

import com.qqj.admin.domain.AdminUser;
import com.qqj.org.controller.legacy.pojo.RegisterTaskListRequest;
import com.qqj.org.enumeration.CustomerAuditStatus;
import com.qqj.org.enumeration.CustomerLevel;
import com.qqj.org.enumeration.CustomerStatus;
import com.qqj.org.facade.OrgFacade;
import com.qqj.org.wrapper.CustomerWrapper;
import com.qqj.org.wrapper.TeamWrapper;
import com.qqj.org.wrapper.TmpCustomerWrapper;
import com.qqj.purchase.controller.PurchaseListRequest;
import com.qqj.purchase.enumeration.PurchaseAuditStatus;
import com.qqj.purchase.facade.PurchaseFacade;
import com.qqj.purchase.wrapper.PurchaseWrapper;
import com.qqj.response.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Created by wangguodong on 16/4/12.
 */
@Controller
public class AdminOrgController {

    @Autowired
    private OrgFacade orgFacade;

    @Autowired
    private PurchaseFacade purchaseFacade;

    @RequestMapping(value = "/org/team/list", method = RequestMethod.GET)
    @ResponseBody
    public Response<TeamWrapper> teams(@CurrentAdminUser AdminUser admin, TeamListRequest request) {
        return orgFacade.getTeamList(request);
    }

    @RequestMapping(value = "/org/team/all", method = RequestMethod.GET)
    @ResponseBody
    public List<TeamWrapper> teams() {
        return orgFacade.getAllTeams();
    }

    @RequestMapping(value = "/org/team/add", method = RequestMethod.POST)
    @ResponseBody
    public void addTeam(@RequestBody TeamRequest request) {
        orgFacade.addTeam(request);
    }

    @RequestMapping(value = "/org/customer/list", method = RequestMethod.GET)
    @ResponseBody
    public Response<CustomerWrapper> customers(CustomerListRequest request) {
        return orgFacade.getCustomerList(request);
    }

    @RequestMapping(value = "/org/founder/add", method = RequestMethod.POST)
    @ResponseBody
    public Response addFounder(@RequestBody CustomerRequest request) {
        return orgFacade.addFounder(request);
    }

    @RequestMapping(value = "/org/customer/edit", method = RequestMethod.PUT)
    @ResponseBody
    public Response editCustomer(@PathVariable("id") Long id, @RequestBody CustomerRequest request) {
        return orgFacade.editCustomer(id, request);
    }

    @RequestMapping(value = "/org/customer/{id}", method = RequestMethod.GET)
    @ResponseBody
    public CustomerWrapper getCustomer(@PathVariable("id") Long id) {
        return orgFacade.getCustomer(id);
    }

    @RequestMapping(value = "/org/customer/level-enumeration", method = RequestMethod.GET)
    @ResponseBody
    public CustomerLevel[] getCustomerLevelEnumeration() {
        return CustomerLevel.values();
    }

    @RequestMapping(value = "/org/customer/status-enumeration", method = RequestMethod.GET)
    @ResponseBody
    public CustomerStatus[] getCustomerStatusEnumeration() {
        return CustomerStatus.values();
    }

    @RequestMapping(value = "/org/tmp-customer/status-enumeration", method = RequestMethod.GET)
    @ResponseBody
    public CustomerAuditStatus[] getTmpCustomerStatusEnumeration() {
        return CustomerAuditStatus.values();
    }

    @RequestMapping(value = "/org/customer/register-tasks", method = RequestMethod.GET)
    @ResponseBody
    public Response<TmpCustomerWrapper> getRegisterTasks(@CurrentAdminUser AdminUser admin, RegisterTaskListRequest request) {
        return orgFacade.getTmpCustomerWrappers(null, request);
    }

    @RequestMapping(value = "/org/customer/register-task/{id}", method = RequestMethod.GET)
    @ResponseBody
    public TmpCustomerWrapper getRegisterTask(@PathVariable("id") Long id) {
        return orgFacade.getTmpCustomerWrapper(id);
    }

    //代理审批
    @RequestMapping(value = "/org/customer/audit", method = RequestMethod.POST)
    @ResponseBody
    public Response auditCustomer(@CurrentAdminUser AdminUser adminUser, @RequestBody AuditCustomerRequest request) {
        return orgFacade.auditCustomer(request);
    }

    @RequestMapping(value = "/org/purchase/status-enumeration", method = RequestMethod.GET)
    @ResponseBody
    public PurchaseAuditStatus[] getPurchaseStatusEnumeration() {
        return PurchaseAuditStatus.values();
    }

    @RequestMapping(value = "/org/purchase/list", method = RequestMethod.GET)
    @ResponseBody
    public Response<PurchaseWrapper> getPurchaseList(@CurrentAdminUser AdminUser admin, PurchaseListRequest request) {
        return purchaseFacade.getPurchaseList(null, request);
    }
}
