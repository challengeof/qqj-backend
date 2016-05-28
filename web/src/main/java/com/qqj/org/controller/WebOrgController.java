package com.qqj.org.controller;

import com.qqj.error.CustomerNotExistsException;
import com.qqj.org.controller.legacy.pojo.TmpCustomerListRequest;
import com.qqj.org.domain.Customer;
import com.qqj.org.enumeration.CustomerAuditStatus;
import com.qqj.org.enumeration.CustomerLevel;
import com.qqj.org.enumeration.CustomerStatus;
import com.qqj.org.facade.CustomerFacade;
import com.qqj.org.facade.OrgFacade;
import com.qqj.org.service.CustomerService;
import com.qqj.org.wrapper.CustomerWrapper;
import com.qqj.org.wrapper.TeamWrapper;
import com.qqj.org.wrapper.TmpCustomerWrapper;
import com.qqj.response.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;


@Controller
public class WebOrgController {
    private static Logger logger = LoggerFactory.getLogger(WebOrgController.class);

    @Autowired
    private OrgFacade orgFacade;

    @Autowired
    private CustomerService customerService;

    @Autowired
    private CustomerFacade customerFacade;

    @Autowired
    private AuthenticationManager authenticationManager;

    @RequestMapping(value = {"/api/customer"}, method = RequestMethod.GET)
    @ResponseBody
    public CustomerWrapper profile(Principal principal) {
        Customer customer = customerService.findCustomerByUsername(principal.getName());
        customerService.update(customer);
        return new CustomerWrapper(customer);
    }

    //代理注册养颜
    @RequestMapping(value = "/api/register", method = RequestMethod.POST)
    @ResponseBody
    public Response register(@CurrentCustomer Customer parent, @RequestBody CustomerRequest customerRequest) {
        return orgFacade.register(parent, customerRequest);
    }

    //代理获取下级
    @RequestMapping(value = "/api/customers", method = RequestMethod.GET)
    @ResponseBody
    public Response<CustomerWrapper> getCustomers(@CurrentCustomer Customer parent, CustomerListRequest customerListRequest) {
        customerListRequest.setParent(parent.getId());
        return orgFacade.getCustomerList(customerListRequest);
    }

    //代理或团队创始人获取未完成审批的下级
    @RequestMapping(value = "/api/tmp-customers", method = RequestMethod.GET)
    @ResponseBody
    public Response<TmpCustomerWrapper> getTmpCustomers(@CurrentCustomer Customer currentCustomer, TmpCustomerListRequest request) {
        return orgFacade.getTmpCustomers(currentCustomer, request);
    }

    //代理审批
    @RequestMapping(value = "/api/customer/audit", method = RequestMethod.POST)
    @ResponseBody
    public Response auditCustomer(@CurrentCustomer Customer customer, @RequestBody AuditCustomerRequest request) {
        return orgFacade.auditCustomer(request);
    }

    @RequestMapping(value = "/api/customer/level-enumeration", method = RequestMethod.GET)
    @ResponseBody
    public CustomerLevel[] getCustomerLevelEnumeration() {
        return CustomerLevel.values();
    }

    @RequestMapping(value = "/api/tmp-customer/status-enumeration", method = RequestMethod.GET)
    @ResponseBody
    public CustomerAuditStatus[] getTmpCustomerStatusEnumeration() {
        return CustomerAuditStatus.values();
    }

    @RequestMapping(value = "/api/customer/status-enumeration", method = RequestMethod.GET)
    @ResponseBody
    public CustomerStatus[] getCustomerStatusEnumeration() {
        return CustomerStatus.values();
    }

    @RequestMapping(value = "/api/team/all", method = RequestMethod.GET)
    @ResponseBody
    public List<TeamWrapper> teams() {
        return orgFacade.getAllTeams();
    }

    @RequestMapping(value = "/api/{username}/reset-password", method = RequestMethod.PUT)
    @ResponseBody
    public void resetPassword(@PathVariable("username") String telephone,
                                 @RequestParam("code") String code,
                                 @RequestParam("password") String password) {
        final Customer customer = customerService.findCustomerByUsername(telephone);
        if (customer == null) {
            throw new CustomerNotExistsException();
        }

        customerService.updateCustomerPassword(customer, password);
    }

//    @RequestMapping(value = "/api/v2/restaurant/updatePassword", method = RequestMethod.POST)
//    @ResponseBody
//    public int updateCustomerPassword(@RequestParam("username") String username, @RequestParam("password") String password,@RequestParam("newpassword") String newPassword) {
//        final Customer customer = customerService.findCustomerByUsername(username);
//        String oldPassword = customerService.getReformedPassword(username, password);
//
//        if(customer != null && customer.getPassword().equals(oldPassword)){
//            return customerFacade.updatePassword(username, newPassword) ? 1 : 2;
//        }else {
//            return 3;
//        }
//    }

    @RequestMapping(value = "/api/v2/available",
            method = {
                    RequestMethod.GET,
                    RequestMethod.HEAD,
                    RequestMethod.POST,
                    RequestMethod.PUT,
                    RequestMethod.PATCH,
                    RequestMethod.DELETE,
                    RequestMethod.OPTIONS,
                    RequestMethod.TRACE,
            })
    @ResponseBody
    public void webAvilable() {}
}
