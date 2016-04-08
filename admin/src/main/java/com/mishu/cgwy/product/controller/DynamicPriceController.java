package com.mishu.cgwy.product.controller;

import com.mishu.cgwy.admin.controller.CurrentAdminUser;
import com.mishu.cgwy.admin.domain.AdminUser;
import com.mishu.cgwy.product.facade.DynamicPriceFacade;
import com.mishu.cgwy.product.facade.DynamicPriceTempFacade;
import com.mishu.cgwy.product.facade.ProductEdbFacade;
import com.mishu.cgwy.product.facade.ProductExcelFacade;
import com.mishu.cgwy.product.vo.DynamicSkuPriceVo;
import com.mishu.cgwy.product.wrapper.ChangeDetailWrapper;
import com.mishu.cgwy.product.wrapper.DynamicSkuPriceFullWrapper;
import com.mishu.cgwy.product.wrapper.DynamicSkuPriceWrapper;
import com.mishu.cgwy.product.wrapper.SimpleSkuWrapper;
import com.mishu.cgwy.response.query.QueryResponse;
import com.mishu.cgwy.task.util.ExportExcelUtils;
import com.mishu.cgwy.utils.UserDateEditor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * User: xudong
 * Date: 4/14/15
 * Time: 11:29 PM
 */
@Controller
public class DynamicPriceController {

    @Autowired
    private DynamicPriceFacade dynamicPriceFacade;

    @Autowired
    private ProductExcelFacade productExcelFacade;

    @Autowired
    private DynamicPriceTempFacade dynamicPriceTempFacade;
    
    @Autowired
    private ProductEdbFacade productEdbFacade;

    @InitBinder
    public void initBinder(WebDataBinder binder) {
        binder.registerCustomEditor(Date.class, new UserDateEditor());
    }

    @RequestMapping(value = "/api/dynamic-price", method = RequestMethod.GET)
    @ResponseBody
    public QueryResponse<DynamicSkuPriceVo> queryDynamicPrice(DynamicPriceQueryRequest request,
                                                       @CurrentAdminUser AdminUser adminUser) {
        return dynamicPriceFacade.queryDynamicPrice(request, adminUser);
    }

    @RequestMapping(value = "/api/dynamic-price/unique", method = RequestMethod.GET)
    @ResponseBody
    public DynamicSkuPriceWrapper queryUniqueDynamicPrice(@RequestParam("skuId") Long skuId,
                                                          @RequestParam("warehouseId") Long warehouseId,
                                                          @CurrentAdminUser AdminUser adminUser) {
        return dynamicPriceFacade.queryUniqueDynamicPrice(skuId, warehouseId, adminUser);
    }


    @RequestMapping(value = "/api/dynamic-price/{id}", method = RequestMethod.GET)
    @ResponseBody
    public DynamicSkuPriceWrapper getDynamicPrice(@PathVariable("id") Long id,
                                                  @CurrentAdminUser AdminUser adminUser) {
        return dynamicPriceFacade.getDynamicPrice(id, adminUser);
    }

    @RequestMapping(value = "/api/dynamic-price-temp", method = RequestMethod.POST)
    @ResponseBody
    public ChangeDetailWrapper updateDynamicPriceTemp(@RequestBody DynamicPriceRequest request,
                                                      @CurrentAdminUser AdminUser adminUser) {
        return new ChangeDetailWrapper(dynamicPriceTempFacade.updateDynamicPriceTemp(request, adminUser));
    }

    @RequestMapping(value = "/api/dynamic-price-temp/fast", method = RequestMethod.POST)
    @ResponseBody
    public ChangeDetailWrapper fastUpdateDynamicPriceTemp(@RequestBody DynamicPriceRequest request,
                                                          @CurrentAdminUser AdminUser adminUser) {
        return new ChangeDetailWrapper(dynamicPriceTempFacade.fastUpdateDynamicPriceTemp(request, adminUser));
    }

    @RequestMapping(value = "/api/dynamic-price/excelImport", method = RequestMethod.POST)
    @ResponseBody
    public Map<String, Object> excelImport(@RequestParam("file") MultipartFile file, @RequestParam("organizationId") Long organizationId,
                                           @CurrentAdminUser AdminUser adminUser) {
        try {
            return productExcelFacade.dynamicPriceExcelImport(file, organizationId, adminUser);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @RequestMapping(value = "/api/dynamic-price/excelExport", method = RequestMethod.GET)
    @ResponseBody
    public HttpEntity<byte[]> excelExport(DynamicPriceQueryRequest request, @CurrentAdminUser AdminUser adminUser)
            throws IOException {
        request.setPageSize(Integer.MAX_VALUE);
        String fileDir = "excel";
        String fileName = "workbook.xls";
        File file = productExcelFacade.dynamicPriceExcelExport(request, adminUser, fileDir, fileName);
        return ExportExcelUtils.getHttpEntityXlsx(file.getPath());
    }

    @RequestMapping(value = "/api/dynamic-price/errorFile", method = RequestMethod.GET)
    @ResponseBody
    public HttpEntity<byte[]> downloadErrorFile(String errorFileName) {
        return ExportExcelUtils.getHttpEntityXlsx(errorFileName);
    }

    @RequestMapping(value = "/api/dynamic-price/{id}", method = RequestMethod.POST)
    @ResponseBody
    public void updateDynamicPrice(@PathVariable("id") Long id, @RequestParam("status") Long
            status, @CurrentAdminUser AdminUser adminUser) throws Exception {
        dynamicPriceTempFacade.updateDynamicSkuPrice(id, status, adminUser);
    }

    @RequestMapping(value = "/api/dynamic-price-temp", method = RequestMethod.GET)
    @ResponseBody
    public DynamicPriceTempQueryResponse queryDynamicPriceTemp(ProductOrDynamicPriceQueryRequest request,
                                                       @CurrentAdminUser AdminUser adminUser) {
        return dynamicPriceTempFacade.queryDynamicPriceTemp(request, adminUser);
    }
    
    @RequestMapping(value = "/api/dynamic-price-edb/{id}", method = RequestMethod.PUT)
    @ResponseBody
    public String synchronizeToEdb(@PathVariable("id") Long id) {
    	return productEdbFacade.updateProduct(id);
    }

    @RequestMapping(value = "/api/dynamic-price/batchUpdate", method = RequestMethod.POST)
    @ResponseBody
    public void updateDynamicPrice(@RequestBody UpdateProductBatchRequest request,@CurrentAdminUser AdminUser adminUser) throws Exception{
        Collections.sort(request.getChangeDetailIds());
        for(Long id : request.getChangeDetailIds()){
            dynamicPriceTempFacade.updateDynamicSkuPrice(id, request.getStatus(), adminUser);
        }
    }
    @RequestMapping(value = "/api/dynamic-price/candidates" , method = RequestMethod.GET)
    @ResponseBody
    public List<DynamicSkuPriceWrapper> getDynamicPriceCandidates(DynamicPriceCandidatesRequest request){
        return dynamicPriceFacade.getDynamicPriceCandidates(request);
    }

    @RequestMapping(value = "/api/dynamic-price/candidatesPlus" , method = RequestMethod.GET)
    @ResponseBody
    public List<SimpleSkuWrapper> getDynamicPriceCandidatesPlus(DynamicPriceCandidatesRequest request){
        return dynamicPriceFacade.getSkuPlus(request);
    }

    @RequestMapping(value = "/api/dynamic-price/sku", method = RequestMethod.GET)
    @ResponseBody
    public DynamicSkuPriceFullWrapper getDynamicPriceByWarehouse(@RequestParam("skuId") Long skuId,
                                                  @RequestParam(value = "warehouse") Long warehouse) {
        return dynamicPriceFacade.findBySkuIdAndWarehouseId(skuId, warehouse);
    }
}
