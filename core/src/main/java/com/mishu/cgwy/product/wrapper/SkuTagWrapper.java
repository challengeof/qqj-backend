package com.mishu.cgwy.product.wrapper;

import com.mishu.cgwy.common.wrapper.SimpleCityWrapper;
import com.mishu.cgwy.product.domain.SkuTag;
import com.mishu.cgwy.product.vo.SkuVo;
import lombok.Data;

/**
 * Created by wangwei on 15/12/2.
 */
@Data
@Deprecated
public class SkuTagWrapper {

    private SkuWrapper sku;
    private SimpleCityWrapper city;
    private Boolean inDiscount;
    private Integer limitedQuantity;

    public SkuTagWrapper(){}

    public SkuTagWrapper(SkuTag skuTag){
        sku = new SkuWrapper(skuTag.getSku());
        city = new SimpleCityWrapper(skuTag.getCity());
        inDiscount = skuTag.getInDiscount();
        limitedQuantity = skuTag.getLimitedQuantity();
    }
}
