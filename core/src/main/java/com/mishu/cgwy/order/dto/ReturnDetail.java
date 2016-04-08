package com.mishu.cgwy.order.dto;

import lombok.Data;

import java.math.BigDecimal;

/**
 * Created by bowen on 15-4-27.
 */
@Data
public class ReturnDetail {

    /** 商品id */
    private Long productId;
    /** 商品编码 */
    private String productNumber;
    /** 价格 */
    private BigDecimal price;
    /** 数量 */
    private int number;
    /** 类型 1：破损 2：无损 3：少货 4：错货 */
    private int type;
    /** 名称 */
    private String name;
    /** 是否再次下单 1：yes 2：no */
    private int reCreate;
}
