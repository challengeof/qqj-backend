package com.mishu.cgwy.profile.wrapper;

import com.mishu.cgwy.profile.domain.Caller;
import com.mishu.cgwy.profile.domain.Restaurant;
import lombok.Data;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by king-ck on 2015/9/29.
 */
@Data
public class CallerWrapper {


    private Long id;

    private String phone;

    private String name;

    private String detail;

    private Date createDate;

    private Date modifyDate;



//    private List<RestaurantWrapper> restaurants;

    public CallerWrapper(Caller caller){

        this.id=caller.getId();
        this.phone=caller.getPhone();
        this.name=caller.getName();
        this.detail=caller.getDetail();
        this.createDate=caller.getCreateDate();
        this.modifyDate=caller.getModifyDate();

//        if(caller.getRestaurants()!=null) {
//            List<RestaurantWrapper> restaurantWrappers = new ArrayList<>();
//            for(Restaurant restaurant : caller.getRestaurants()){
//                RestaurantWrapper restaurantWrapper=new RestaurantWrapper(restaurant);
//                restaurantWrappers.add(restaurantWrapper);
//            }
//            this.restaurants=restaurantWrappers;
//        }
    }
}
