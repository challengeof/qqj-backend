package com.mishu.cgwy.product.controller;

import com.mishu.cgwy.product.facade.ProductFacade;
import com.mishu.cgwy.product.wrapper.BrandWrapper;
import com.mishu.cgwy.product.wrapper.CompleteSkuWrapper;
import com.mishu.cgwy.product.wrapper.SkuWrapper;
import com.mishu.cgwy.profile.controller.CurrentCustomer;
import com.mishu.cgwy.profile.domain.Customer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;


@Controller
public class WebProductController {

    @Autowired
    private ProductFacade productFacade;


    @RequestMapping(value = "/api/v2/sku/{skuId}", method = RequestMethod.GET)
    @ResponseBody
    public CompleteSkuWrapper getProduct(@PathVariable("skuId") Long skuId, @CurrentCustomer Customer customer) {
        return productFacade.getSku(skuId, customer);
    }

    @RequestMapping(value = "/api/v2/sku", method = RequestMethod.GET)
    @ResponseBody
    public CustomerProductQueryResponse searchProductResponse(CustomerProductQueryRequest request, @CurrentCustomer Customer customer) {
        return productFacade.getSkus(request, customer);
    }

    @RequestMapping(value = "/api/v2/sku/brand", method = RequestMethod.GET)
    @ResponseBody
    public CustomerProductBrandResponse groupBrands(CustomerProductQueryRequest request, @CurrentCustomer Customer
            customer) {
        return productFacade.groupBrands(request, customer);
    }

}
