package com.qqj.org.facade;

import com.qqj.org.domain.Customer;
import com.qqj.org.service.CustomerService;
import com.qqj.org.wrapper.CustomerWrapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * User: xudong
 * Date: 3/3/15
 * Time: 6:58 PM
 */
@Service
public class CustomerFacade {

    private static Logger logger = LoggerFactory.getLogger(CustomerFacade.class);

    @Autowired
    private CustomerService customerService;

    @Autowired(required = false)
    private AuthenticationManager authenticationManager;

    @Transactional
    public Customer findCustomerByUsername(String username) {
        return customerService.findCustomerByUsername(username);
    }

    @Transactional
    public CustomerWrapper findCustomerWrapperByUsername(String username) {
        return new CustomerWrapper(customerService.findCustomerByUsername(username));
    }

    @Transactional
    public boolean updatePassword(String username, String newPassword) {
        Customer customer = findCustomerByUsername(username);
        if (null != customer) {
            customerService.updateCustomerPassword(customer, newPassword);
            return true;
        }
        return false;
    }

    @Transactional
    public boolean updatePassword(Customer customer, String oldPassword, String newPassword) {

        // 兼容旧密码格式
        String compatibleOldPassword = customer.getUsername() + oldPassword + "mirror";
        String compatibleNewPassword = customer.getUsername() + newPassword + "mirror";

        UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(customer.getUsername(),
                compatibleOldPassword);

        try {
            Authentication auth = authenticationManager.authenticate(token);

            if (auth.isAuthenticated()) {
                customerService.updateCustomerPassword(customer, compatibleNewPassword);

                return true;
            }
        } catch (AuthenticationException e) {
        }
        return false;
    }

}
