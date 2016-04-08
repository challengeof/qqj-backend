package com.mishu.cgwy.profile.controller;

import lombok.Data;

/**
 * Created by king-ck on 2016/3/3.
 */
@Data
public class BlockAutoRequest {
    private Long cityId;
    private Double lat; //纬度
    private Double lng; //经度
}
