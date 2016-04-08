package com.mishu.cgwy.profile.controller.caller;

import com.mishu.cgwy.profile.domain.Caller;
import com.mishu.cgwy.profile.domain.Restaurant;
import com.mishu.cgwy.profile.wrapper.CallerWrapper;
import com.mishu.cgwy.profile.wrapper.RestaurantWrapper;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by king-ck on 2015/9/29.
 */
@Data
public class CallerQueryResponse {

    private String phone;

    private List<RestaurantWrapper> otherRestaurant;

    private CallerWrapper caller;

    public CallerQueryResponse(){}

    public CallerQueryResponse(String phone , Caller caller,List<Restaurant> restaurants){
        this.phone=phone;
        if(caller==null && restaurants==null){
            return ;
        }
        if(restaurants!=null){
            List<RestaurantWrapper> restaurantWrappers = new ArrayList<>();
            for(Restaurant restaurant : restaurants){
                RestaurantWrapper restaurantWrapper=new RestaurantWrapper(restaurant);
                restaurantWrappers.add(restaurantWrapper);
            }
            this.otherRestaurant=restaurantWrappers;
        }

        if(caller!=null){
            this.caller=new CallerWrapper(caller);
        }
    }


}
