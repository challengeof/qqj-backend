package com.qqj.profile.controller;

import com.qqj.org.domain.Customer;
import com.qqj.org.facade.CustomerFacade;
import org.springframework.core.MethodParameter;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import java.security.Principal;

public class CurrentCustomerHandlerMethodArgumentResolver implements
        HandlerMethodArgumentResolver {

    private CustomerFacade customerFacade;

    @Override
    public boolean supportsParameter(MethodParameter methodParameter) {
        return methodParameter.getParameterAnnotation(CurrentCustomer.class) != null
                && methodParameter.getParameterType().equals(Customer.class);
    }

    @Override
    public Object resolveArgument(MethodParameter methodParameter,
                                  ModelAndViewContainer mavContainer, NativeWebRequest webRequest,
                                  WebDataBinderFactory binderFactory) throws Exception {

        if (this.supportsParameter(methodParameter)) {
            Principal principal = webRequest.getUserPrincipal();

            if (principal != null) {
                Customer customer = customerFacade.findCustomerByUsername(principal.getName());
                return customer;
            }
        }
        return null;
    }

    public void setCustomerFacade(CustomerFacade customerFacade) {
        this.customerFacade = customerFacade;
    }
}
