package com.qqj.org.controller;

import com.qqj.org.enumeration.CustomerLevel;
import com.qqj.org.enumeration.CustomerStatus;
import com.qqj.org.facade.OrgFacade;
import com.qqj.org.wrapper.CustomerWrapper;
import com.qqj.org.wrapper.TeamWrapper;
import com.qqj.response.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

/**
 * Created by wangguodong on 16/4/12.
 */
@Controller
public class OrgController {

    @Autowired
    private OrgFacade orgFacade;

    @RequestMapping(value = "/org/team/list", method = RequestMethod.GET)
    @ResponseBody
    public Response<TeamWrapper> teams(TeamListRequest request) {
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

    @RequestMapping(value = "/org/customer/add", method = RequestMethod.POST)
    @ResponseBody
    public Response addCustomer(@RequestBody CustomerRequest request) {
        return orgFacade.addCustomer(request);
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
}
