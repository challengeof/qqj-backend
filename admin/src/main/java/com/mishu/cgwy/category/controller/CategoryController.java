package com.mishu.cgwy.category.controller;

import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;
import com.mishu.cgwy.product.domain.CategoryStatus;
import com.mishu.cgwy.product.facade.ProductFacade;
import com.mishu.cgwy.product.wrapper.CategoryWrapper;
import com.mishu.cgwy.utils.TreeJsonHasChild;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

/**
 * User: xudong
 * Date: 3/18/15
 * Time: 3:33 PM
 */
@Controller
public class CategoryController {

    @Autowired
    private ProductFacade productFacade;

    @RequestMapping(value = "/api/category/status", method = RequestMethod.GET)
    @ResponseBody
    public CategoryStatus[] listCategoryStatus() {
        return CategoryStatus.values();
    }

    @RequestMapping(value = "/api/category", method = RequestMethod.POST)
    @ResponseBody
    public CategoryWrapper createCategory(@RequestBody CategoryRequest categoryRequest) {
        if (Long.valueOf(0).equals(categoryRequest.getParentCategoryId())) {
            categoryRequest.setParentCategoryId(null);
        }

        return productFacade.createCategory(categoryRequest);
    }

    @RequestMapping(value = "/api/category/{id}", method = RequestMethod.PUT)
    @ResponseBody
    public CategoryWrapper updateCategory(@PathVariable("id") Long id, @RequestBody CategoryRequest categoryRequest) {
        if (Long.valueOf(0).equals(categoryRequest.getParentCategoryId())) {
            categoryRequest.setParentCategoryId(null);
        }

        return productFacade.updateCategory(id, categoryRequest);
    }


    @RequestMapping(value = "/api/category/{id}/children", method = RequestMethod.GET)
    @ResponseBody
    public List<CategoryWrapper> listCategories(@PathVariable("id") Long parentId, @RequestParam(value = "status", required = false) final Integer status) {
        return productFacade.listCategories(parentId, status);
    }

    @RequestMapping(value = "/api/category/{id}/children", method = RequestMethod.PUT)
    @ResponseBody
    public CategoryWrapper updateCategoryChildren(@PathVariable("id") Long parentId, @RequestParam(value = "children") final Long[] children) {
        return productFacade.updateCategoryChildren(parentId, children);
    }


    @RequestMapping(value = "/api/category/treeJson", method = RequestMethod.GET)
    @ResponseBody
    public List<TreeJsonHasChild> getCategoryTree(@RequestParam(value = "status", required = false) Integer status) {
        return productFacade.getCategoriesTree(0L, status);
    }

    @RequestMapping(value = "/api/category/{id}/changeCity", method = RequestMethod.PUT)
    @ResponseBody
    public void setCategoryCity(@PathVariable(value = "id") Long categoryId, @RequestParam(value = "cityId") Long cityId, @RequestParam(value = "active") Boolean active) {
        productFacade.setCategoryCity(categoryId, cityId, active);
    }

    @RequestMapping(value = "/api/category", method = RequestMethod.GET)
    @ResponseBody
    public List<CategoryWrapper> listAllCategories(
            @RequestParam(value = "status", required = false) final Integer status) {


        final List<CategoryWrapper> all = productFacade.listAllCategories();

        if (status == null) {
            return all;
        } else {
            return new ArrayList<>(Collections2.filter(all, new Predicate<CategoryWrapper>() {
                @Override
                public boolean apply(CategoryWrapper input) {
                    return input.getStatus().getValue().equals(status);
                }
            }));
        }
    }

    @RequestMapping(value = "/api/category/{id}", method = RequestMethod.GET)
    @ResponseBody
    public CategoryWrapper getCategory(@PathVariable("id") Long id) {
        return productFacade.getCategory(id);
    }

    @Deprecated
    @RequestMapping(value = "/api/firstLevel/category", method = RequestMethod.GET)
    @ResponseBody
    public List<CategoryWrapper> getFirstLevelCategory() {
        return productFacade.listCategories(null, CategoryStatus.ACTIVE.getValue());
    }
}
