package com.mishu.cgwy.order.controller;

import com.mishu.cgwy.product.vo.SkuVo;
import com.mishu.cgwy.product.wrapper.SkuWrapper;
import lombok.Data;

import java.math.BigDecimal;

/** 验货App查看订单包
 * Created by bowen on 15/8/13.
 */
@Data
public class OrderGroupsSkuTotal {

    private SkuWrapper sku;

    private BigDecimal price;

    private Integer quantity;

}
