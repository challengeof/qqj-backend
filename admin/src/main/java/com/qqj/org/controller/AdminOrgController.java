package com.qqj.org.controller;

import com.qqj.admin.domain.AdminUser;
import com.qqj.org.controller.legacy.pojo.RegisterTaskListRequest;
import com.qqj.org.enumeration.CustomerAuditStatus;
import com.qqj.org.enumeration.CustomerLevel;
import com.qqj.org.enumeration.CustomerStatus;
import com.qqj.org.facade.OrgFacade;
import com.qqj.org.wrapper.CustomerWrapper;
import com.qqj.org.wrapper.TeamWrapper;
import com.qqj.org.wrapper.RegisterTaskWrapper;
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

    @RequestMapping(value = "/org//api/register-tasks", method = RequestMethod.GET)
    @ResponseBody
    public Response<RegisterTaskWrapper> getRegisterTasks(@CurrentAdminUser AdminUser admin, RegisterTaskListRequest request) {
        return orgFacade.getRegisterTasks(null, request);
    }

    @RequestMapping(value = "/org/api/register-task/{id}", method = RequestMethod.GET)
    @ResponseBody
    public RegisterTaskWrapper getRegisterTask(@PathVariable("id") Long id) {
        return orgFacade.getRegisterTask(id);
    }

    //代理审批
    @RequestMapping(value = "/org/api/customer/audit", method = RequestMethod.POST)
    @ResponseBody
    public Response auditCustomer(@CurrentAdminUser AdminUser adminUser, @RequestBody AuditCustomerRequest request) {
        return orgFacade.auditCustomer(request);
    }
}
