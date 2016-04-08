package com.mishu.cgwy.conf.vo;

import com.mishu.cgwy.common.domain.City;
import com.mishu.cgwy.common.vo.CityVo;
import com.mishu.cgwy.common.wrapper.SimpleCityWrapper;
import lombok.Data;

@Data
public class OrderLimit {

    private CityVo city;

    private Long limit;
}
