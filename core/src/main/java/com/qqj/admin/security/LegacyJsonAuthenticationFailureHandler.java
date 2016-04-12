package com.qqj.admin.security;

import com.qqj.error.RestError;
import com.qqj.utils.RenderUtils;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;

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