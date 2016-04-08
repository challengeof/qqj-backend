package com.mishu.cgwy.vendor.controller;

import com.mishu.cgwy.admin.service.VendorService;
import com.mishu.cgwy.inventory.domain.Vendor;
import org.springframework.core.MethodParameter;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import java.security.Principal;

public class CurrentVendorHandlerMethodArgumentResolver implements
        HandlerMethodArgumentResolver {

    private VendorService vendorService;

    @Override
    public boolean supportsParameter(MethodParameter methodParameter) {
        return methodParameter.getParameterAnnotation(CurrentVendor.class) != null && methodParameter.getParameterType().equals(Vendor.class);
    }

    @Override
    public Object resolveArgument(MethodParameter methodParameter,
                                  ModelAndViewContainer mavContainer, NativeWebRequest webRequest,
                                  WebDataBinderFactory binderFactory) throws Exception {

        if (this.supportsParameter(methodParameter)) {
            Principal principal = webRequest.getUserPrincipal();

            if (principal != null) {
                Vendor vendor = vendorService.findVendorByUsername(principal.getName());
                return vendor;
            }
        }
        return null;
    }

    public void setVendorService(VendorService vendorService) {
        this.vendorService = vendorService;
    }
}
