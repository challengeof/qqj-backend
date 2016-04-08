package com.mishu.cgwy.product.controller;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;
import com.mishu.cgwy.common.domain.City;
import com.mishu.cgwy.common.service.LocationService;
import com.mishu.cgwy.product.constants.Constants;
import com.mishu.cgwy.product.domain.Category;
import com.mishu.cgwy.product.domain.CategoryStatus;
import com.mishu.cgwy.product.facade.ProductFacade;
import com.mishu.cgwy.product.service.ProductService;
import com.mishu.cgwy.product.wrapper.ActiveCascadeCategoryWrapper;
import com.mishu.cgwy.product.wrapper.CategoryWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * User: xudong
 * Date: 5/21/15
 * Time: 11:42 PM
 */
@Controller
public class CategoryController {

    @Autowired
    private ProductFacade productFacade;

    @Autowired
    private LocationService locationService;


    @RequestMapping(value = "/api/v2/category", method = RequestMethod.GET)
    @ResponseBody
    public List<ActiveCascadeCategoryWrapper> getCategories() {
        final List<Category> activeTopCategories = productFacade.getSubCategories(null, CategoryStatus.ACTIVE.getValue());

        return new ArrayList<>(Collections2.transform(activeTopCategories, new Function<Category, ActiveCascadeCategoryWrapper>() {
            @Override
            public ActiveCascadeCategoryWrapper apply(Category input) {
                return new ActiveCascadeCategoryWrapper(input);
            }

        }));
    }

    @RequestMapping(value = "/api/v2/new/category", method = RequestMethod.GET)
    @ResponseBody
    public List<CategoryWrapper> getNewCategories(@RequestParam(value = "cityId", required = false) Long cityId) {
        City city = null;
        if (cityId != null && cityId != 0) {
            city = locationService.getCity(cityId);
        } else {
            city = locationService.getCity(Constants.DEFAULT_CITY);
        }

        List<Category> activeTopCategories = productFacade.getSubCategories(null, CategoryStatus.ACTIVE.getValue());

        List<CategoryWrapper> list = new ArrayList<>();

        for (Category category : activeTopCategories) {
            for (Category category1 : category.getChildrenCategories()) {
                if (validateCategory(category1, city)) {
                    CategoryWrapper firstLevel = new CategoryWrapper(category1);
                    firstLevel.setMainParentCategoryId(firstLevel.getId());
                    list.add(firstLevel);
                    for (Category category2 : category1.getChildrenCategories()) {
                        if (validateCategory(category2, city)) {
                            CategoryWrapper secondLevel = new CategoryWrapper(category2);
                            secondLevel.setMainParentCategoryId(firstLevel.getId());
                            firstLevel.getChildren().add(secondLevel);
                            for (Category category3 : category2.getChildrenCategories()) {
                                if (validateCategory(category3, city)) {
                                    CategoryWrapper thirdLevel = new CategoryWrapper(category3);
                                    thirdLevel.setMainParentCategoryId(firstLevel.getId());
                                    secondLevel.getChildren().add(thirdLevel);
                                    if (category3.getShowSecond() != null && category3.getShowSecond().equals(Boolean.TRUE)) {
                                        firstLevel.getChildren().add(thirdLevel);
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        return list;
    }

    private boolean validateCategory(Category category, City city) {
        if (category != null && category.getStatus() == CategoryStatus.ACTIVE.getValue() && category.getCities().contains(city)) {
            return true;
        }
        return false;
    }
}
