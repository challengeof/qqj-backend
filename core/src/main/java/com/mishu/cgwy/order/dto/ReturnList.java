package com.mishu.cgwy.order.dto;

import lombok.Data;

import java.math.BigDecimal;

/**
 * Created by bowen on 15-5-5.
 */
@Data
public class ReturnList {
    /** 价格 */
    private BigDecimal price;
    /** 数量 */
    private int number;
    /** 产品编号 */
    private String productNumber;
    /** 名称 */
    private String name;
    /** 图片 */
    private String url;
    /** 商品id */
    private Long productId;
}
