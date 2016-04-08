package com.mishu.cgwy.admin.security;

import com.mishu.cgwy.admin.facade.AdminUserFacade;
import com.mishu.cgwy.error.RestError;
import com.mishu.cgwy.utils.RenderUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class LegacyJsonAuthenticationFailureHandler implements
        AuthenticationFailureHandler{

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException, ServletException {
        final RestError data = new RestError();
        data.setErrno(21401);
        RenderUtils.renderJson(response, data);
    }
}