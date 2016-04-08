package com.mishu.cgwy.carRoute.vo;

import com.mishu.cgwy.carRoute.domain.CarRoute;
import com.mishu.cgwy.common.domain.City;
import com.mishu.cgwy.stock.domain.Depot;
import lombok.Data;

/**
 * Created by xgl on 2016/04/06.
 */
@Data
public class CarRouteVo {
    private Long id;
    private String name;
    private Double price;
    private String cityName;//城市
    private String depotName;//仓库 -- 区域
    private String operator;//操作人\

    private Long cityId;
    private Long depotId;

    public CarRouteVo(){}

    public CarRouteVo(CarRoute carRoute){
        this.id = carRoute.getId();
        this.name = carRoute.getName();
        this.price = carRoute.getPrice();
        this.cityName = carRoute.getCity().getName();
        this.depotName = carRoute.getDepot().getName();
        this.operator = carRoute.getOperator().getRealname();
        this.cityId = carRoute.getCity().getId();
        this.depotId = carRoute.getDepot().getId();
    }
}
