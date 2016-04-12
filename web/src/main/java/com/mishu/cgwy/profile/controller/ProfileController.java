package com.mishu.cgwy.profile.controller;

import com.mishu.cgwy.error.CustomerAlreadyExistsException;
import com.mishu.cgwy.error.CustomerNotExistsException;
import com.mishu.cgwy.profile.domain.Customer;
import com.mishu.cgwy.profile.facade.CustomerFacade;
import com.mishu.cgwy.profile.service.CustomerService;
import com.mishu.cgwy.profile.wrapper.CustomerWrapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.security.Principal;


/**
 * User: xudong
 * Date: 3/1/15
 * Time: 3:30 PM
 */
@Controller
public class ProfileController {
    private static Logger logger = LoggerFactory.getLogger(ProfileController.class);

    @Autowired
    private CustomerService customerService;

    @Autowired
    private CustomerFacade customerFacade;

    @Autowired
    private AuthenticationManager authenticationManager;

    @RequestMapping(value = "/api/v2/check-username", method = RequestMethod.GET)
    @ResponseBody
    public void checkUsername(@RequestParam("username") String username) {
        Customer customer = customerFacade.findCustomerByUsername(username);
        if (customer != null) {
            throw new CustomerAlreadyExistsException();
        }
    }

    @Secured("ROLE_USER")
    @RequestMapping(value = {"/api/v2/customer"}, method = RequestMethod.GET)
    @ResponseBody
    public CustomerWrapper profile(Principal principal) {
        Customer customer = customerService.findCustomerByUsername(principal.getName());
        customerService.update(customer);
        return new CustomerWrapper(customer);
    }

    @RequestMapping(value = "/api/v2/register", method = RequestMethod.POST)
    @ResponseBody
    public RegisterResponse register(@RequestBody RegisterRequest registerRequest, HttpServletRequest request) {
        final RegisterResponse register = customerFacade.register(registerRequest);
        UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(
                registerRequest.getTelephone(), registerRequest.getTelephone() + registerRequest.getPassword() + "mirror");
        try {
            token.setDetails(new WebAuthenticationDetails(request));
            Authentication authenticatedUser = authenticationManager.authenticate(token);
            SecurityContextHolder.getContext().setAuthentication(authenticatedUser);
            request.getSession().setAttribute(HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY, SecurityContextHolder.getContext());
        } catch (AuthenticationException e) {
            logger.warn("fail to auto login after register", e);
        }

        return register;
    }

    @RequestMapping(value = "/api/v2/{username}/reset-password", method = RequestMethod.PUT)
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
