package com.mishu.cgwy.stock.dto;

import lombok.Data;

import java.util.Set;

@Data
public class ShelfData {

    private Long id;

    private Long depotId;

    private String name;

    private String area;

    private String row;

    private String number;

    private String shelfCode;

    private Set<Long> shelfIds;

    private Integer areaNum;

    private Integer rowNum;

    private Integer numberNum;
}
