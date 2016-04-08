package com.mishu.cgwy.stock.dto;

import lombok.Data;

import java.math.BigDecimal;

/**
 * Created by wangguodong on 15/9/16.
 */
@Data
public class TransferItemData {

    private Long id;

    private Long skuId;

    private Integer quantity;
}
