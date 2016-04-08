package com.mishu.cgwy.product.wrapper;

import com.mishu.cgwy.common.wrapper.SimpleCityWrapper;
import com.mishu.cgwy.product.domain.SkuTag;
import lombok.Data;

/**
 * Created by wangwei on 15/12/2.
 */
@Deprecated
@Data
public class SimpleSkuTagWrapper {

    private Long skuId;
    private SimpleCityWrapper city;
    private Boolean inDiscount;
    private Integer limitedQuantity;

    public SimpleSkuTagWrapper(){}

    public SimpleSkuTagWrapper(SkuTag skuTag){
        skuId = skuTag.getSku().getId();
        city = new SimpleCityWrapper(skuTag.getCity());
        inDiscount = skuTag.getInDiscount();
        limitedQuantity = skuTag.getLimitedQuantity();
    }
}
