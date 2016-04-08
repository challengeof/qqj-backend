package com.mishu.cgwy.product.constants;

import com.fasterxml.jackson.annotation.JsonFormat;

/**
 * @author chengzheng
 */
@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum RestaurantStatus {
    UNDEFINED(1, "未审核"), ACTIVE(2, "生效"), INACTIVE(3, "失效"), OUTSIDE(5, "服务区外");

    private Integer value;
    private String name;

    public Integer getValue() {
        return value;
    }

    public String getName() {
        return name;
    }

    private RestaurantStatus(Integer value, String name) {
        this.value = value;
        this.name = name;
    }

    public static RestaurantStatus fromInt(Integer i) {

        if(i!=null){
            for(RestaurantStatus rstatus : RestaurantStatus.values()){
                if(rstatus.getValue().intValue()==i){
                    return rstatus;
                }
            }
        }
        return UNDEFINED;

//        switch (i) {
//            case 1:
//                return UNDEFINED;
//            case 2:
//                return ACTIVE;
//            case 3:
//                return INACTIVE;
//            default:
//                return UNDEFINED;
//        }
    }

}
