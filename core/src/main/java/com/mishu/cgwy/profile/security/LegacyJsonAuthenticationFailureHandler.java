package com.mishu.cgwy.profile.security;

import com.mishu.cgwy.error.RestError;
import com.mishu.cgwy.utils.RenderUtils;
import org.springframework.security.authentication.BadCredentialsException;
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

        if (exception instanceof BadCredentialsException) {
            final RestError data = new RestError();
            data.setErrno(23402);
            data.setErrmsg("密码错误");
            RenderUtils.renderJson(response, data);
        } else {
            final RestError data = new RestError();
            data.setErrno(23401);
            data.setErrmsg("用户不存在");
            RenderUtils.renderJson(response, data);
        }
    }
}