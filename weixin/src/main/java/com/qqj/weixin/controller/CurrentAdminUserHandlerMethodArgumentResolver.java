package com.qqj.weixin.controller;

import com.qqj.admin.service.AdminUserService;
import org.springframework.core.MethodParameter;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

public class CurrentAdminUserHandlerMethodArgumentResolver implements
        HandlerMethodArgumentResolver {

    private AdminUserService adminUserService;

    @Override
    public boolean supportsParameter(MethodParameter methodParameter) {
        return true;
    }

    @Override
    public Object resolveArgument(MethodParameter methodParameter,
                                  ModelAndViewContainer mavContainer, NativeWebRequest webRequest,
                                  WebDataBinderFactory binderFactory) throws Exception {
        return null;
    }
}
