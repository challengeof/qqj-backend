package com.mishu.cgwy.profile.vo;

import com.mishu.cgwy.product.constants.RestaurantStatus;
import com.mishu.cgwy.profile.constants.RestaurantGrade;
import com.mishu.cgwy.profile.domain.RestaurantType;
import lombok.Data;

import java.util.Date;

/**
 * Created by king-ck on 2016/3/1.
 */
@Data
public class CustomerSeaVo {


    private Long customerId;
    private Long restaurantId;
    private String restaurantName;
    private Long blockId;
    private String blockName;
    private String restaurantType;
    private RestaurantGrade grade;

    private RestaurantStatus status;
    private Long devUserId; //开发人员id
    private String devUserName;
    private Long adminUserId; //维护人员id
    private String adminUserName;
    private Date customerCreateDate;
}
