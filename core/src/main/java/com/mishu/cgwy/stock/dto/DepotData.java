package com.mishu.cgwy.stock.dto;

import lombok.Data;

/**
 * Created by admin on 10/8/15.
 */
@Data
public class DepotData {

    private Long id;

    private String name;

    private Boolean isMain;

    private Long cityId;
    private Double latitude;
    private Double longitude;
}
