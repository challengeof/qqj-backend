package com.mishu.cgwy.common.wrapper;

import lombok.Data;

/**
 * Created by xingdong on 15/8/5.
 */
@Data
public class PointWrapper {

    private String longitude;
    private String latitude;

    public PointWrapper(){

    }

    public PointWrapper(String longitude,String latitude){
        this.longitude = longitude;
        this.latitude = latitude;
    }
}
