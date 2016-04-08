package com.mishu.cgwy.car.domain;

import com.mishu.cgwy.admin.domain.AdminUser;
import com.mishu.cgwy.common.domain.City;
import com.mishu.cgwy.common.domain.Warehouse;
import com.mishu.cgwy.stock.domain.Depot;
import lombok.Data;

import javax.persistence.*;
import java.math.BigDecimal;

/**
 * Created by linsen on 15/12/11.
 */

@Entity
@Data
public class Car {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "city_id")
    private City city;

    @ManyToOne
    @JoinColumn(name = "depot_id")
    private Depot depot;

    @OneToOne
    @JoinColumn(name = "admin_user_id")
    private AdminUser adminUser;

    private String name;
    private String expenses;
    private String licencePlateNumber;
    private BigDecimal vehicleLength;
    private BigDecimal vehicleWidth;
    private BigDecimal vehicleHeight;
    private int vehicleModel; //1:轻型封闭货车 2:面包 3:金杯
    private BigDecimal weight;
    private BigDecimal cubic;
    private int status; //0:无效 1:有效
    private String source; //来源
    private String taxingPoint; //税点

}
