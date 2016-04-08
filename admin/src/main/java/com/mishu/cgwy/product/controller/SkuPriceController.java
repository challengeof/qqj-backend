package com.mishu.cgwy.product.controller;

import com.mishu.cgwy.admin.controller.CurrentAdminUser;
import com.mishu.cgwy.admin.domain.AdminUser;
import com.mishu.cgwy.product.domain.RateValue;
import com.mishu.cgwy.product.domain.SkuBundleUnit;
import com.mishu.cgwy.product.domain.SkuSingleUnit;
import com.mishu.cgwy.product.domain.SkuStatus;
import com.mishu.cgwy.product.facade.ProductFacade;
import com.mishu.cgwy.product.facade.SkuFacade;
import com.mishu.cgwy.product.facade.SkuPriceFacade;
import com.mishu.cgwy.product.facade.SkuPriceHistoryFacade;
import com.mishu.cgwy.product.wrapper.*;
import com.mishu.cgwy.response.query.QueryResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
public class SkuPriceController {

    @Autowired
    private SkuPriceFacade skuPriceFacade;

    @Autowired
    private SkuPriceHistoryFacade skuPriceHistoryFacade;

    @RequestMapping(value = "/api/skuPrice/list", method = RequestMethod.GET)
    @ResponseBody
    public QueryResponse<SkuPriceWrapper> getSkuList(SkuPriceListRequest request) {
        return skuPriceFacade.getSkuPriceList(request);
    }

    @RequestMapping(value = "/api/skuPrice/list/export", method = RequestMethod.GET)
    @ResponseBody
    public void exportSkuPriceList(SkuPriceListRequest request,  @CurrentAdminUser AdminUser adminUser) throws Exception {
        skuPriceFacade.asyncExportSkuPriceList(request, adminUser);
    }

    @RequestMapping(value = "/api/skuPriceHistory/list", method = RequestMethod.GET)
    @ResponseBody
    public SkuPriceHistoryQueryResponse<SkuPriceHistoryWrapper> getSkuPriceHistoryList(SkuPriceHistoryListRequest request) {
        return skuPriceHistoryFacade.getSkuPriceHistoryList(request);
    }
}
