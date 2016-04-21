package com.qqj.org.controller;

import com.qqj.error.CustomerNotExistsException;
import com.qqj.org.domain.Customer;
import com.qqj.org.facade.CustomerFacade;
import com.qqj.org.facade.OrgFacade;
import com.qqj.org.service.CustomerService;
import com.qqj.org.wrapper.CustomerWrapper;
import com.qqj.response.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;


@Controller
public class OrgController {
    private static Logger logger = LoggerFactory.getLogger(OrgController.class);

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

    @RequestMapping(value = "/api/register", method = RequestMethod.POST)
    @ResponseBody
    public Response register(@CurrentCustomer Customer parent, @RequestBody CustomerRequest customerRequest) {
        return orgFacade.register(parent, customerRequest);
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

    @RequestMapping(value = "/api/v2/restaurant/updatePassword", method = RequestMethod.POST)
    @ResponseBody
    public int updateCustomerPassword(@RequestParam("username") String username, @RequestParam("password") String password,@RequestParam("newpassword") String newPassword) {
        final Customer customer = customerService.findCustomerByUsername(username);
        String oldPassword = customerService.getReformedPassword(username, password);

        if(customer != null && customer.getPassword().equals(oldPassword)){
            return customerFacade.updatePassword(username, newPassword) ? 1 : 2;
        }else {
            return 3;
        }
    }

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
