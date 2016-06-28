package com.qqj.product.controller;

import com.qqj.admin.domain.AdminUser;
import com.qqj.org.controller.CurrentAdminUser;
import com.qqj.product.enumeration.ProductStatus;
import com.qqj.product.facade.ProductFacade;
import com.qqj.product.wrapper.ProductWrapper;
import com.qqj.response.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Created by wangguodong on 16/6/23.
 */
@Controller
public class ProductController {

    @Autowired
    private ProductFacade productFacade;

    @RequestMapping(value = "/product/list", method = RequestMethod.GET)
    @ResponseBody
    public Response<ProductWrapper> list(@CurrentAdminUser AdminUser admin, ProductListRequest request) {
        return productFacade.list(request);
    }

    @RequestMapping(value = "/product/{id}", method = RequestMethod.GET)
    @ResponseBody
    public ProductWrapper getProduct(@PathVariable("id") Long id) {
        return productFacade.getProduct(id);
    }

    @RequestMapping(value = "/product/status/list", method = RequestMethod.GET)
    @ResponseBody
    public ProductStatus[] statusList() {
        return ProductStatus.values();
    }

    @RequestMapping(value = "/product/list/all", method = RequestMethod.GET)
    @ResponseBody
    public List<ProductWrapper> add() {
        return productFacade.all();
    }

    @RequestMapping(value = "/product/add", method = RequestMethod.POST)
    @ResponseBody
    public void add(@RequestBody ProductRequest request) {
        productFacade.add(request);
    }

    @RequestMapping(value = "/product/modify/{id}", method = RequestMethod.PUT)
    @ResponseBody
    public void add(@PathVariable("id") Long id, @RequestBody ProductRequest request) {
        productFacade.modify(id, request);
    }
}
