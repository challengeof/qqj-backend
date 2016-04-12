package com.qqj.admin.security;

import com.qqj.admin.facade.AdminUserFacade;
import com.qqj.utils.RenderUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class JsonAuthenticationSuccessHandler implements
        AuthenticationSuccessHandler {

    @Autowired
    private AdminUserFacade adminUserFacade;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response, Authentication authentication)
            throws IOException, ServletException {
        if (authentication != null) {
            RenderUtils.renderJson(response, adminUserFacade.getAdminUserByUsername(authentication.getName()));
        }
    }

}