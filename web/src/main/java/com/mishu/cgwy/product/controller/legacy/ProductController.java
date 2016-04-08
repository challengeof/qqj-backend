package com.mishu.cgwy.product.controller.legacy;

import com.mishu.cgwy.product.constants.Constants;
import com.mishu.cgwy.product.controller.legacy.pojo.*;
import com.mishu.cgwy.product.domain.CategoryStatus;
import com.mishu.cgwy.product.dto.*;
import com.mishu.cgwy.product.facade.ProductFacade;
import com.mishu.cgwy.product.wrapper.CategoryWrapper;
import com.mishu.cgwy.product.wrapper.ProductWrapper;
import com.mishu.cgwy.product.wrapper.SkuWrapper;
import com.mishu.cgwy.profile.controller.CurrentCustomer;
import com.mishu.cgwy.profile.domain.Customer;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;


/**
 * Created by kaicheng on 3/18/15.
 */
@Controller("legacyProductController")
public class ProductController {

    @Autowired
    ProductFacade productFacade;

    @RequestMapping(value = "/api/legacy/category", method = RequestMethod.POST)
    @ResponseBody
    public Object categoryDispatcher(@RequestBody CategoryRequest request) {
        //首页分类
        if (Constants.ALL_ALL_CATEGORY.equals(request.getAll())) {
            CategoryAllOrD1Response response = productFacade.buildAllCategoriesResponse();
            return response;
        }

        //一级分类
        else if (request.getCategoryId() != null) {
            CategoryAllOrD1Response response = productFacade.buildD1CategoryResponse(request.getCategoryId());
            return response;
        }

        return null;

    }


    //二级分类信息 GET(文档上是post,抓包是get)
    //url文档是http://root_path/category,按抓包处理
    @RequestMapping(value = "/api/legacy/category/back_search/{id}", method = RequestMethod.GET)
    @ResponseBody
    public CategoryD2Response getCategoryD2(@PathVariable Long id) {
        CategoryD2Response response = productFacade.buildD2CategoryResponse(id);
        return response;
    }

    //商品详情页
    //http://root_path/product/get/{productNumber}
    @RequestMapping(value = "/api/legacy/product/get/{product}", method = RequestMethod.GET)
    @ResponseBody
    public GetProductResponse getProduct(@PathVariable String product, @CurrentCustomer Customer customer) {

        GetProductResponse response = null;
        if (customer == null) //未登录
        {
            response = productFacade.getProductResponse(product, 0L);
        } else {
            if (customer.getBlock() != null)
                response = productFacade.getProductResponse(product, customer.getBlock().getWarehouse().getId());
            else {
                response = productFacade.getProductResponse(product, Constants.DEFAULT_WAREHOUSE);
            }

        }

        return response;
    }

    //搜索
    //http://root_path/product/search
    @RequestMapping(value = "/api/legacy/product/search", method = RequestMethod.POST)
    @ResponseBody
    public SearchProductResponse searchProductResponse(@RequestBody SearchProductRequest request, @CurrentCustomer Customer customer) {
        if (request.getRows() == null || request.getRows().equals(0)) {
            request.setRows(Integer.MAX_VALUE);
        }
        Long brandId = request.getBrandId();
        if (Long.valueOf(0).equals(brandId)) {
            brandId = null;
        }

        // convert 1-index to 0-index
        request.setPage(Math.max(0, request.getPage() - 1));
        SearchProductResponse response = productFacade.searchByName(customer, brandId,
                request.getSort(), request.getOrder(), request.getPage(), request.getRows(), request.getName());
        return response;
    }


    //获取子分类
    //http://root_path/category/json
    @RequestMapping(value = "/api/legacy/category/json", method = RequestMethod.POST)
    @ResponseBody
    public ChildCategoryResponse getChildCategories(@RequestBody CategoryRequest request) {
        if (request.getCategoryId() != null) {
            List<CategoryWrapper> categoryWrappers = productFacade.listCategories(request.getCategoryId(),
                    CategoryStatus.ACTIVE.getValue());

            ChildCategoryResponse response = new ChildCategoryResponse();
            response.setCategories(categoryWrappers);
            return response;

        }
        return null;
    }

    //获取关键词列表
    //http://root_path/product/key_word
    @RequestMapping(value = "/api/legacy/product/key_word", method = RequestMethod.POST)
    @ResponseBody
    public KeywordResponse getKeywordList(@RequestBody KeywordRequest request) {
        if (request.getName() != null) {
            KeywordResponse response = productFacade.getKeywordByQuery(request.getName(), request.getPage(), request.getRows());
            return response;
        }
        return null;
    }

    //获取热销列表
    //http://root_path/product/hot_sell
    @RequestMapping(value = "/api/legacy/product/hot_sell", method = RequestMethod.POST)
    @ResponseBody
    public HotSellResponse getHotsellList(@RequestBody HotSellRequest request, @CurrentCustomer Customer customer) {
        if (request.getProductNumber() != null) {
            HotSellResponse response = productFacade.getHotsellByProductnumber(customer, request.getProductNumber(), request.getPage(),
                    request.getRows());
            return response;
        }
        return null;
    }

}
