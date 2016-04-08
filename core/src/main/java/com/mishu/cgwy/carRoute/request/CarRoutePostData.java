package com.mishu.cgwy.carRoute.request;

import com.mishu.cgwy.carRoute.domain.CarRoute;
import lombok.Data;

/**
 * Created by xgl on 2016/04/06.
 */
@Data
public class CarRoutePostData {
    private Long id;
    private String name;
    private Double price;
    private Long cityId;//城市
    private Long depotId;//仓库 -- 区域

    /*public CarRoutePostData(CarRoute carRoute){
        this.id = carRoute.getId();
        this.name = carRoute.getName();
        this.price = carRoute.getPrice();
        this.cityId = carRoute.getCity().getId();
        this.depotId = carRoute.getDepot().getId();
    }*/
}
