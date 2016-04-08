package com.mishu.cgwy.stock.dto;

import lombok.Data;

@Data
public class ShelfRequest {
    private Long cityId;
    private Long depotId;
    private String name;
    private String area;
    private String row;
    private String number;
    private String shelfCode;
    private int page = 0;
    private int pageSize = 100;
}
