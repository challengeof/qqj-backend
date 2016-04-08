package com.mishu.cgwy.purchase.controller;

import lombok.Data;

@Data
public class ChangeSignRequest {

    private Long cityId;

    private Long depotId;

    private Long[] skuIds;

    private Short sign;
}
