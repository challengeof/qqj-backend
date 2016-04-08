package com.mishu.cgwy.product.wrapper;

import com.mishu.cgwy.common.wrapper.SimpleWarehouseWrapper;
import com.mishu.cgwy.inventory.domain.DynamicSkuPrice;
import com.mishu.cgwy.product.vo.SkuVo;
import lombok.Data;

/**
 * Created by bowen on 15/11/30.
 */
@Data
public class DynamicSkuPriceFullWrapper {

    private Long id;

    private SkuWrapper sku;

    private SimpleWarehouseWrapper warehouse;

    private SingleDynamicSkuPriceStatusWrapper singleDynamicSkuPriceStatus;

    private BundleDynamicSkuPriceStatusWrapper bundleDynamicSkuPriceStatus;

    public DynamicSkuPriceFullWrapper() {

    }

    public DynamicSkuPriceFullWrapper(DynamicSkuPrice dynamicSkuPrice) {
        id = dynamicSkuPrice.getId();
        sku = new SkuWrapper(dynamicSkuPrice.getSku());
        warehouse = new SimpleWarehouseWrapper(dynamicSkuPrice.getWarehouse());
        singleDynamicSkuPriceStatus = new SingleDynamicSkuPriceStatusWrapper(dynamicSkuPrice.getSinglePriceStatus());
        bundleDynamicSkuPriceStatus = new BundleDynamicSkuPriceStatusWrapper(dynamicSkuPrice.getBundlePriceStatus());
    }
}
