package com.mishu.cgwy.profile.controller;

import com.mishu.cgwy.error.RestError;
import com.mishu.cgwy.profile.domain.Customer;
import com.mishu.cgwy.profile.facade.CustomerFacade;
import com.mishu.cgwy.profile.wrapper.FavoriteWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;


/**
 * User: xudong
 * Date: 3/1/15
 * Time: 3:30 PM
 */
@Controller
public class FavoriteController {
    @Autowired
    private CustomerFacade customerFacade;

    @RequestMapping(value = "/api/v2/favorite", method = RequestMethod.GET)
    @ResponseBody
    public List<FavoriteWrapper> getFavorites(@CurrentCustomer Customer customer) {
        return customerFacade.getFavorites(customer);
    }


    @RequestMapping(value = "/api/legacy/favorite", method = RequestMethod.GET)
    @ResponseBody
    public FavoriteQueryResponse legacyGetFavorites(@CurrentCustomer Customer customer) {
        if (customer == null) {
            return new FavoriteQueryResponse();
        } else {
            final FavoriteQueryResponse favoriteQueryResponse = new FavoriteQueryResponse();
            favoriteQueryResponse.setFavorites(customerFacade.getFavorites(customer));
            return favoriteQueryResponse;
        }
    }

    @RequestMapping(value = "/api/legacy/favorite", method = RequestMethod.PUT)
    @ResponseBody
    public RestError legacyAddFavorite(@CurrentCustomer Customer customer, @RequestParam("skuId") Long skuId) {
        if (customer == null) {
            return new RestError();
        } else {
            customerFacade.addFavorite(customer, skuId);
            return new RestError();
        }
    }

    @RequestMapping(value = "/api/v2/favorite", method = RequestMethod.PUT)
    @ResponseBody
    public FavoriteWrapper addFavorite(@CurrentCustomer Customer customer, @RequestParam("skuId") Long skuId) {
            return  customerFacade.addFavorite(customer, skuId);
    }

    @RequestMapping(value = {"/api/legacy/favorite", "/api/v2/favorite"}, method = RequestMethod.DELETE)
    @ResponseBody
    public RestError deleteFavorite(@CurrentCustomer Customer customer, @RequestParam("skuId") List<Long> skuIds) {
        if (customer == null) {
            return new RestError();
        } else {
            customerFacade.deleteFavorite(customer, skuIds);
            return new RestError();
        }
    }


}
