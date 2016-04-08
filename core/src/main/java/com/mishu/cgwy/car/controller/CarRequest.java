package com.mishu.cgwy.car.controller;

/**
 * Created by linsen on 15/12/11.
 */
import lombok.Data;

import java.math.BigDecimal;

@Data
public class CarRequest {

    private Long id;
    private Long depotId;
    private Long cityId;
    private Long trackerId;
    private String name;

    private String licencePlateNumber;
    private BigDecimal vehicleLength;
    private BigDecimal vehicleWidth;
    private BigDecimal vehicleHeight;
    private int vehicleModel; //0:轻型封闭货车 1:面包 2:金杯
    private BigDecimal weight;
    private BigDecimal cubic;
    private Integer status; //0:无效 1:有效
    private String expenses;
    private String source; //来源
    private String taxingPoint;

    private int page;
    private int pageSize = 100;
}
