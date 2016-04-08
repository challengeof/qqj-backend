package com.mishu.cgwy.product.controller;

import java.io.IOException;
import java.util.*;

import com.mishu.cgwy.product.vo.ProductVo;
import com.mishu.cgwy.product.vo.SkuVo;
import com.mishu.cgwy.utils.UserDateEditor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mishu.cgwy.admin.controller.CurrentAdminUser;
import com.mishu.cgwy.admin.domain.AdminUser;
import com.mishu.cgwy.product.constants.CheckStatus;
import com.mishu.cgwy.product.constants.Constants;
import com.mishu.cgwy.product.domain.ChangeDetail;
import com.mishu.cgwy.product.dto.ProductRequest;
import com.mishu.cgwy.product.facade.ProductExcelFacade;
import com.mishu.cgwy.product.facade.ProductFacade;
import com.mishu.cgwy.product.facade.ProductTempFacade;
import com.mishu.cgwy.product.wrapper.ChangeDetailWrapper;
import com.mishu.cgwy.product.wrapper.ProductWrapper;
import com.mishu.cgwy.utils.ZipFileUtils;

/**
 * User: xudong
 * Date: 3/20/15
 * Time: 10:56 AM
 */
@Controller
public class ProductController {
    @Autowired
    private ProductFacade productFacade;

    @Autowired
    private ProductExcelFacade productExcelFacade;

    @Autowired
    private ProductTempFacade productTempFacade;

    @InitBinder
    public void initBinder(WebDataBinder binder) {
        binder.registerCustomEditor(Date.class, new UserDateEditor());
    }

    @Secured("PRODUCT_ADMIN")
    @RequestMapping(value = "/api/product-temp", method = RequestMethod.POST)
    @ResponseBody
    public ChangeDetailWrapper createProductTemp(@RequestBody ProductRequest productRequest,@CurrentAdminUser AdminUser submitter) {
        return new ChangeDetailWrapper(productTempFacade.createProductTemp(productRequest, submitter));
    }

    @Secured("PRODUCT_ADMIN")
    @RequestMapping(value = "/api/product/{id}", method = RequestMethod.GET)
    @ResponseBody
    public ProductVo getProduct(@PathVariable("id") Long id) {
        return productFacade.getProduct(id);
    }

    @Secured("PRODUCT_ADMIN")
    @RequestMapping(value = "/api/productTemp/{id}", method = RequestMethod.PUT)
    @ResponseBody
    public ChangeDetailWrapper updateProductTemp(@PathVariable("id") Long id, @RequestBody ProductRequest
            productRequest,@CurrentAdminUser AdminUser submitter) {
        return new ChangeDetailWrapper(productTempFacade.updateProductTemp(id, productRequest, submitter));
    }

    @RequestMapping(value = "/api/product/excelImport", method = RequestMethod.POST)
    @ResponseBody
    public Map<String, Object> excelImport(@RequestParam("file") MultipartFile file, @RequestParam("organizationId") Long organizationId ,
                                           @CurrentAdminUser AdminUser adminUser) {

        try {
            return productExcelFacade.productExcelImport(file, organizationId, adminUser);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @RequestMapping(value = "/api/product/photoImportByName", method = RequestMethod.POST)
    @ResponseBody
    public Map<String, Object> photoImportByName(@RequestParam("file") MultipartFile file, @RequestParam("organizationId") Long organizationId,
                                                 @CurrentAdminUser AdminUser adminUser) {

        try {
            return productExcelFacade.productPhotoImport(file, organizationId, adminUser, 1);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @RequestMapping(value = "/api/product/photoImportById", method = RequestMethod.POST)
    @ResponseBody
    public Map<String, Object> photoImportById(@RequestParam("file") MultipartFile file, @RequestParam("organizationId") Long organizationId,
                                               @CurrentAdminUser AdminUser adminUser) {

        try {
            return productExcelFacade.productPhotoImport(file, organizationId, adminUser, 2);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Secured("PRODUCT_ADMIN")
    @RequestMapping(value = "/api/product/{id}", method = RequestMethod.POST)
    @ResponseBody
    public void updateProduct(@PathVariable("id") Long id, @RequestParam("status") Long status,@CurrentAdminUser AdminUser verifier) {
        productTempFacade.updateProduct(id, status, verifier);
    }

    @Secured("PRODUCT_ADMIN")
    @RequestMapping(value = "/api/product-temp", method = RequestMethod.GET)
    @ResponseBody
    public ProductTempQueryResponse getProductTemps(ProductOrDynamicPriceQueryRequest request, @CurrentAdminUser AdminUser adminUser) {
        return productTempFacade.findProductTemps(request, adminUser);
    }

    @RequestMapping(value = "/api/check/status", method = RequestMethod.GET)
    @ResponseBody
    public List<CheckStatus> listCheckStatus() {
        final CheckStatus[] values = CheckStatus.values();
        final List<CheckStatus> list = new ArrayList<>(Arrays.asList(values));
        return list;
    }

    @Secured("PRODUCT_ADMIN")
    @RequestMapping(value = "/api/product-temp/{id}", method = RequestMethod.GET)
    @ResponseBody
    public ChangeDetailResponse getProductTemp(@PathVariable("id") Long id) {
        return productTempFacade.getProductTemp(id);
    }
    @Secured("PRODUCT_ADMIN")
    @RequestMapping(value = "/api/product/batchUpdate", method = RequestMethod.POST)
    @ResponseBody
    public ProductBatchResponse updateProductBatch(@RequestBody UpdateProductBatchRequest request,@CurrentAdminUser AdminUser verifier) {
        ProductBatchResponse response = new ProductBatchResponse();
        int errorNum = 0;
        Collections.sort(request.getChangeDetailIds());
        for(Long id:request.getChangeDetailIds()){
            try {
                 productTempFacade.updateProduct(id, request.getStatus(), verifier);
            }catch(Exception e){
                errorNum++;
                continue;
            }
        }
        response.setErrorNum(errorNum);
        return response;
    }

    @RequestMapping(value = "/api/product/errorFile", method = RequestMethod.GET)
    @ResponseBody
    public HttpEntity<byte[]> downloadFile(String fileName) {
        return ZipFileUtils.getHttpEntityZip(fileName);
    }
}
