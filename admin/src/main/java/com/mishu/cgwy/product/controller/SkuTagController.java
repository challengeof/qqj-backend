package com.mishu.cgwy.product.controller;

import com.mishu.cgwy.product.facade.SkuTagFacade;
import com.mishu.cgwy.product.wrapper.SkuTagWrapper;
import com.mishu.cgwy.response.query.QueryResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Created by wangwei on 15/12/2.
 */
@Controller
public class SkuTagController {

    @Autowired
    private SkuTagFacade skuTagFacade;

    @RequestMapping(value = "/api/skuTag", method = RequestMethod.GET)
    @ResponseBody
    public QueryResponse<SkuTagWrapper> getSkuTags(SkuTagQueryRequest request) {
        return skuTagFacade.getSkuTag(request);
    }

    @RequestMapping(value = "/api/skuTag/sku/{id}", method = RequestMethod.PUT)
    @ResponseBody
    public void updateSkuTags(@PathVariable(value = "id") Long skuId, @RequestParam(value = "cityIds", required = false) List<Long> cityIds, @RequestParam(value = "limitedQuantity", required = false) Integer limitedQuantity, @RequestParam(value = "limitedCityIds", required = false) List<Long> limitedCityIds) {
        skuTagFacade.updateSkuTag(skuId, cityIds, limitedQuantity,limitedCityIds);
    }

}
