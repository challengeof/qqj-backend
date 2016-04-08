package com.mishu.cgwy.product.controller;

import com.mishu.cgwy.request.Request;
import lombok.Data;

@Data
public class SkuPriceListRequest extends Request {

    private Long categoryId;

    private Long cityId;

    private Long organizationId;

    private Long warehouseId;

    private Long vendorId;

    private Integer status;

    private Long productId;

    private Long skuId;

    private String productName;

    private Short type;

    private int page = 0;

    private int pageSize = 100;
}
