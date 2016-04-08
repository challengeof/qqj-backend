package com.mishu.cgwy.banner.controller;

import com.mishu.cgwy.banner.dto.BannerUrl;
import lombok.Data;

import java.util.Date;

/**
 * Created by bowen on 15-7-28.
 */
@Data
public class BannerRequest {

    private Long cityId;

//    private List<Long> warehouseIds = new ArrayList<Long>();

    private Long warehouseId;

    private String description;

    private Date start;

    private Date end;

    private BannerUrl bannerUrl;

    private Integer orderValue;
}
