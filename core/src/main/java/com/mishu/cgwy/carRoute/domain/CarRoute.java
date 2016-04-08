package com.mishu.cgwy.carRoute.domain;

import com.mishu.cgwy.admin.domain.AdminUser;
import com.mishu.cgwy.common.domain.City;
import com.mishu.cgwy.stock.domain.Depot;
import lombok.Data;

import javax.persistence.*;

/**
 * Created by xgl on 2016/04/05.
 */
@Entity
@Data
public class CarRoute {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private Double price;

    @ManyToOne
    @JoinColumn(name = "city_id")
    private City city;//城市

    @ManyToOne
    @JoinColumn(name = "depot_id")
    private Depot depot;//仓库 -- 区域

    @ManyToOne
    @JoinColumn(name = "operator")
    private AdminUser operator;
}
