package com.mishu.cgwy.product.controller;

import com.mishu.cgwy.admin.controller.CurrentAdminUser;
import com.mishu.cgwy.admin.domain.AdminUser;
import com.mishu.cgwy.product.domain.RateValue;
import com.mishu.cgwy.product.domain.SkuBundleUnit;
import com.mishu.cgwy.product.domain.SkuSingleUnit;
import com.mishu.cgwy.product.domain.SkuStatus;
import com.mishu.cgwy.product.facade.ProductFacade;
import com.mishu.cgwy.product.facade.SkuFacade;
import com.mishu.cgwy.product.vo.SkuVo;
import com.mishu.cgwy.product.wrapper.CandidateSkuWrapper;
import com.mishu.cgwy.product.wrapper.SkuVendorWrapper;
import com.mishu.cgwy.response.query.QueryResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * User: xudong
 * Date: 3/20/15
 * Time: 10:56 AM
 */
@Controller
public class SkuController {
    @Autowired
    private ProductFacade productFacade;

    @Autowired
    private SkuFacade skuFacade;


    @Secured("PRODUCT_ADMIN")
    @RequestMapping(value = "/api/sku", method = RequestMethod.GET)
    @ResponseBody
    public SkuQueryResponse findSkus(SkuQueryRequest request) {
        return productFacade.findSkus(request);
    }

    @Secured("PRODUCT_ADMIN")
    @RequestMapping(value = "/api/sku/status", method = RequestMethod.GET)
    @ResponseBody
    public SkuStatus[] listProductStatus() {
        return SkuStatus.values();
    }

    @RequestMapping(value = "/api/sku/skuSingleUnit", method = RequestMethod.GET)
    @ResponseBody
    public SkuSingleUnit[] listSkuSingleUnit() {
        return SkuSingleUnit.values();
    }

    @RequestMapping(value = "/api/sku/skuBundleUnit", method = RequestMethod.GET)
    @ResponseBody
    public SkuBundleUnit[] listSkuBundleUnit() {
        return SkuBundleUnit.values();
    }

    @RequestMapping(value = "/api/sku/rateValues", method = RequestMethod.GET)
    @ResponseBody
    public RateValue[] listRateValue() {
        return RateValue.values();
    }

    @RequestMapping(value = "/api/skuVendor/{id}", method = RequestMethod.GET)
    @ResponseBody
    public SkuVendorWrapper getSkuVendor(@PathVariable("id") Long id) {
        return skuFacade.getSkuVendor(id);
    }

    @RequestMapping(value = "/api/skuVendor/list", method = RequestMethod.GET)
    @ResponseBody
    public QueryResponse<SkuVendorWrapper> findSkus(SkuListRequest request) {
        return skuFacade.getSkuList(request);
    }

    @RequestMapping(value = "/api/sku/updateSkuVendor", method = RequestMethod.POST)
    @ResponseBody
    public void updateDynamicPrice(@RequestBody SkuVendorRequest request, @CurrentAdminUser AdminUser operator) {
        skuFacade.updateSkuVendor(request, operator);
    }

    @RequestMapping(value = "/api/sku-price/excelImport", method = RequestMethod.POST)
    @ResponseBody
    public Map<String,Object> excelImport(
            @RequestParam("file") MultipartFile file, @RequestParam("cityId") Long cityId, @CurrentAdminUser AdminUser adminUser) throws Exception {
        Map map = new HashMap();
        map.put("message", skuFacade.skuPriceExcelImport(file, cityId, adminUser));
        return map;
    }

    @RequestMapping(value = "/api/sku-price/downloadTemplate", method = RequestMethod.GET)
    @ResponseBody
    public HttpEntity<byte[]> downloadTemplate() throws Exception {
        return skuFacade.downloadTemplate();
    }

    @RequestMapping(value = "/api/sku/candidates",method = RequestMethod.GET)
    @ResponseBody
    public List<CandidateSkuWrapper> getSkuCandidates(SkuCandidatesRequest request){
        return skuFacade.getSkuCandidates(request);
    }


    @RequestMapping(value = "/api/sku/query/specify",method = RequestMethod.GET)
    @ResponseBody
    public List<SkuVo> getSkuVos(SkuSpecifyQueryRequest request){

        return skuFacade.getSkus(request.getSkuIds());
    }


    @RequestMapping(value = "/api/sku/capacityInBundle/{id}", method = RequestMethod.PUT)
    @ResponseBody
    public void updateSkuCapacityInBundle(@PathVariable("id") Long skuId, @RequestParam(value = "capacityInBundle", required = true) Integer capacityInBundle) {
        skuFacade.updateSkuCapacityInBundle(skuId, capacityInBundle);
    }
}
