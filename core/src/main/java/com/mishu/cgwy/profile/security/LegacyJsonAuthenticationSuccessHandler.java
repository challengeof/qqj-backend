package com.mishu.cgwy.profile.security;

import com.mishu.cgwy.profile.controller.legacy.pojo.LoginResponse;
import com.mishu.cgwy.profile.domain.Customer;
import com.mishu.cgwy.profile.service.CustomerService;
import com.mishu.cgwy.profile.wrapper.CustomerWrapper;
import com.mishu.cgwy.utils.RenderUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class LegacyJsonAuthenticationSuccessHandler implements
        AuthenticationSuccessHandler {

    @Autowired
    private CustomerService customerService;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response, Authentication authentication)
            throws IOException, ServletException {
        if (authentication != null) {
            Customer customer = customerService.findCustomerByUsername(authentication.getName());

            LoginResponse loginResponse = new LoginResponse();
            loginResponse.setUserId(customer.getId());
            loginResponse.setUsername(customer.getUsername());
            loginResponse.setUserNumber(customer.getUserNumber());

            RenderUtils.renderJson(response, loginResponse);
        } else {
            LoginResponse loginResponse = new LoginResponse();
            loginResponse.setErrno(-1);
            loginResponse.setErrmsg("用户名或密码错误");
            RenderUtils.renderJson(response, loginResponse);
        }
    }

}