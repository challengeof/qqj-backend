package com.mishu.cgwy.carRoute.request;

import com.mishu.cgwy.common.domain.City;
import com.mishu.cgwy.stock.domain.Depot;
import lombok.Data;

/**
 * Created by xgl on 2016/04/06.
 */
@Data
public class CarRouteRequestQuery {
    private Long id;
    private String name;
    private Double price;
    private Long cityId;//城市
    private Long depotId;//仓库 -- 区域
    private int page = 0;
    private int pageSize = 15;
}
