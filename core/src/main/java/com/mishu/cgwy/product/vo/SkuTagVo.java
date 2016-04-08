package com.mishu.cgwy.product.vo;

import com.mishu.cgwy.common.wrapper.SimpleCityWrapper;
import com.mishu.cgwy.product.domain.SkuTag;
import lombok.Data;

/**
 * Created by wangwei on 15/12/2.
 */
@Data
public class SkuTagVo {

    private Long skuId;
    private Long cityId;
    private String cityName;
    private Boolean inDiscount;
    private Integer limitedQuantity;
}
