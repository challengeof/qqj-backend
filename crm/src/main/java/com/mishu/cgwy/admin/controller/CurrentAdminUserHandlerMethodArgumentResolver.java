package com.mishu.cgwy.admin.controller;

import com.mishu.cgwy.admin.domain.AdminUser;
import com.mishu.cgwy.admin.service.AdminUserService;
import org.springframework.core.MethodParameter;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import java.security.Principal;

public class CurrentAdminUserHandlerMethodArgumentResolver implements
        HandlerMethodArgumentResolver {

    private AdminUserService adminUserService;

    @Override
    public boolean supportsParameter(MethodParameter methodParameter) {
        return methodParameter.getParameterAnnotation(CurrentAdminUser.class) != null
                && methodParameter.getParameterType().equals(AdminUser.class);
    }

    @Override
    public Object resolveArgument(MethodParameter methodParameter,
                                  ModelAndViewContainer mavContainer, NativeWebRequest webRequest,
                                  WebDataBinderFactory binderFactory) throws Exception {

        if (this.supportsParameter(methodParameter)) {
            Principal principal = webRequest.getUserPrincipal();

            if (principal != null) {
                AdminUser adminUser = adminUserService.findAdminUserByUsername(principal.getName());
                return adminUser;
            }
        }
        return null;
    }

    public void setAdminUserService(AdminUserService adminUserService) {
        this.adminUserService = adminUserService;
    }
}
