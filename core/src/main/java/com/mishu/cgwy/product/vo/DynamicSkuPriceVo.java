package com.mishu.cgwy.product.vo;

import com.mishu.cgwy.common.vo.WarehouseVo;
import com.mishu.cgwy.common.wrapper.SimpleWarehouseWrapper;
import com.mishu.cgwy.inventory.domain.DynamicSkuPrice;
import com.mishu.cgwy.product.wrapper.BundleDynamicSkuPriceStatusWrapper;
import com.mishu.cgwy.product.wrapper.SimpleSkuWrapper;
import com.mishu.cgwy.product.wrapper.SingleDynamicSkuPriceStatusWrapper;
import lombok.Data;

import java.math.BigDecimal;

/**
 * User: xudong
 * Date: 4/14/15
 * Time: 5:10 PM
 */
@Data
public class DynamicSkuPriceVo {

	private Long id;

    private SkuVo sku;

    private BigDecimal fixedPrice;

    private WarehouseVo warehouse;

    private SingleDynamicSkuPriceStatusVo singleDynamicSkuPriceStatus;

    private BundleDynamicSkuPriceStatusVo bundleDynamicSkuPriceStatus;

    private BigDecimal singleSalePriceLimit;

    private BigDecimal bundleSalePriceLimit;
}
