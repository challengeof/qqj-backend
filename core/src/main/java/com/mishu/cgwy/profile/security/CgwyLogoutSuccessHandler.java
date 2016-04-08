package com.mishu.cgwy.profile.security;

import com.mishu.cgwy.profile.controller.legacy.pojo.LogoutResponse;
import com.mishu.cgwy.utils.RenderUtils;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * User: xudong
 * Date: 4/24/15
 * Time: 12:55 PM
 */
public class CgwyLogoutSuccessHandler implements LogoutSuccessHandler {
    @Override
    public void onLogoutSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        RenderUtils.renderJson(response, new LogoutResponse());
    }
}
