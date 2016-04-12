package com.qqj.admin.security;

import com.google.common.base.Function;
import com.google.common.collect.Collections2;
import com.qqj.admin.domain.AdminRole;
import com.qqj.admin.domain.AdminUser;
import com.qqj.admin.facade.AdminUserFacade;
import com.qqj.utils.RenderUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;

public class LegacyJsonAuthenticationSuccessHandler implements
        AuthenticationSuccessHandler {

    @Autowired
    private AdminUserFacade adminUserFacade;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response, Authentication authentication)
            throws IOException, ServletException {
        if (authentication != null) {
            AdminUser adminUser = adminUserFacade.getAdminUserEntityByUsername(authentication.getName());
            LegacyLoginResponse legacyLoginResponse = new LegacyLoginResponse();
            legacyLoginResponse.setUsername(adminUser.getUsername());
            legacyLoginResponse.setAdminId(adminUser.getId());
            legacyLoginResponse.setRoleList(new ArrayList<Long>(Collections2.transform(adminUser.getAdminRoles(), new Function<AdminRole, Long>() {
                @Override
                public Long apply(AdminRole input) {
                    return input.getId();
                }
            })));
//            legacyLoginResponse.setOrganization(adminUser.getOrganization());
            RenderUtils.renderJson(response, legacyLoginResponse);
        }
    }

}