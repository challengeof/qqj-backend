package com.mishu.cgwy.product.wrapper;

import com.mishu.cgwy.common.wrapper.SimpleWarehouseWrapper;
import com.mishu.cgwy.inventory.domain.DynamicSkuPrice;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * User: xudong
 * Date: 4/14/15
 * Time: 5:10 PM
 */
@Data
@Deprecated
public class DynamicSkuPriceWrapper {

	private Long id;
	
    private SimpleSkuWrapper sku;

    private BigDecimal fixedPrice;

    private SimpleWarehouseWrapper warehouse;

//    这里防止价格导入时候空指针异常
    private SingleDynamicSkuPriceStatusWrapper singleDynamicSkuPriceStatus = new SingleDynamicSkuPriceStatusWrapper();

    private BundleDynamicSkuPriceStatusWrapper bundleDynamicSkuPriceStatus = new BundleDynamicSkuPriceStatusWrapper();

    private BigDecimal singleSalePriceLimit;

    private BigDecimal bundleSalePriceLimit;

    private boolean effectType;

    private Date effectTime;

    public DynamicSkuPriceWrapper() {

    }

    public DynamicSkuPriceWrapper(DynamicSkuPrice dynamicSkuPrice) {
        id = dynamicSkuPrice.getId();
        sku = new SimpleSkuWrapper(dynamicSkuPrice.getSku());
        warehouse = new SimpleWarehouseWrapper(dynamicSkuPrice.getWarehouse());
        singleDynamicSkuPriceStatus = new SingleDynamicSkuPriceStatusWrapper(dynamicSkuPrice.getSinglePriceStatus());
        bundleDynamicSkuPriceStatus = new BundleDynamicSkuPriceStatusWrapper(dynamicSkuPrice.getBundlePriceStatus());
    }
}
