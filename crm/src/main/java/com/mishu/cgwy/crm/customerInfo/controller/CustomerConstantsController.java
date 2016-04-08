package com.mishu.cgwy.crm.customerInfo.controller;

import com.mishu.cgwy.profile.constants.*;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Created by king-ck on 2016/3/18.
 */
@Controller
public class CustomerConstantsController {

    /***********************************************************************/

    /**
     * 获取合作状态
     */
    @RequestMapping(value = "/api/customerInfo/constants/cooperatingState", method = RequestMethod.GET)
    @ResponseBody
    public RestaurantCooperatingState[] getCooperatingStates(){
        return RestaurantCooperatingState.values();
    }


    /**
     * 查询角色
     */
    @RequestMapping(value = "/api/customerInfo/constants/restaurantSellerType", method = RequestMethod.GET)
    @ResponseBody
    public RestaurantSellerType[] getRestaurantSellerTypes(){

        return RestaurantSellerType.values();
    }

    /**
     * 跟进状态
     */
    @RequestMapping(value = "/api/customerInfo/constants/customerFollowUpStatus", method = RequestMethod.GET)
    @ResponseBody
    public CustomerFollowUpStatus[] getCustomerFollowUpStatus(){

        return CustomerFollowUpStatus.values();
    }

    /**
     * 审核状态  RestaurantReviewStatus
     */
    @RequestMapping(value = "/api/customerInfo/constants/restaurantReviewStatus", method = RequestMethod.GET)
    @ResponseBody
    public RestaurantReviewStatus[] getRestaurantReviewStatus(){

        return RestaurantReviewStatus.values();
    }

    /**
     * 客户类型
     */
    @RequestMapping(value = "/api/customerInfo/constants/restaurantActiveType", method = RequestMethod.GET)
    @ResponseBody
    public RestaurantActiveType[] getCustomerActiveTypes(){

        return RestaurantActiveType.values();
    }


}
