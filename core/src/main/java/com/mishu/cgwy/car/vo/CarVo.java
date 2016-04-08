package com.mishu.cgwy.car.vo;

import com.mishu.cgwy.admin.domain.AdminUser;
import com.mishu.cgwy.admin.vo.AdminUserVo;
import com.mishu.cgwy.car.domain.Car;
import com.mishu.cgwy.common.domain.City;
import com.mishu.cgwy.common.vo.CityVo;
import com.mishu.cgwy.stock.domain.Depot;
import com.mishu.cgwy.stock.vo.DepotVo;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class CarVo {
    private Long id;
    private String name;
    private CityVo city;
    private DepotVo depot;
    private AdminUserVo adminUser;

    private String licencePlateNumber;
    private BigDecimal vehicleLength;
    private BigDecimal vehicleWidth;
    private BigDecimal vehicleHeight;
    private int vehicleModel; //0:轻型封闭货车 1:面包 2:金杯
    private BigDecimal weight;
    private BigDecimal cubic;
    private int status; //0:无效 1:有效
    private String expenses;
    private String source; //来源
    private String taxingPoint; //税点
}
