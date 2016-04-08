package com.mishu.cgwy.product.controller;

import com.mishu.cgwy.product.dto.BrandData;
import com.mishu.cgwy.product.dto.BrandRequest;
import com.mishu.cgwy.product.facade.BrandFacade;
import com.mishu.cgwy.product.facade.ProductFacade;
import com.mishu.cgwy.product.wrapper.BrandWrapper;
import com.mishu.cgwy.response.query.QueryResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * User: xudong
 * Date: 4/13/15
 * Time: 12:53 PM
 */
@Controller
public class BrandController {

    @Deprecated
    @Autowired
    private ProductFacade productFacade;
    @Autowired
    private BrandFacade brandFacade;

    @Deprecated
    @RequestMapping(value = "/api/brand", method = RequestMethod.GET)
    @ResponseBody
    public List<BrandWrapper> listAllBrands() {
        return productFacade.findAllBrands();
    }

    @RequestMapping(value = "/api/brand/list", method = RequestMethod.GET)
    @ResponseBody
    public QueryResponse<BrandWrapper> getBrandList(BrandRequest request) {
        return brandFacade.getBrandList(request);
    }

    @RequestMapping(value = "/api/brand/update", method = RequestMethod.POST)
    @ResponseBody
    public BrandWrapper updateBrand(@RequestBody BrandData brandData) {
        return brandFacade.updateBrand(brandData);
    }

    @RequestMapping(value = "/api/brand/{id}", method = RequestMethod.GET)
    @ResponseBody
    public BrandWrapper getBrandById(@PathVariable("id") Long id) {
        return brandFacade.getBrandById(id);
    }

}
