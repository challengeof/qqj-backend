package com.mishu.cgwy.vendor.wrapper;

import com.mishu.cgwy.product.domain.Sku;
import com.mishu.cgwy.vendor.domain.VendorOrderItem;
import lombok.Data;

import java.math.BigDecimal;

/**
 * Created by wangguodong on 15/12/15.
 */
@Data
public class VendorOrderHistory {

    private Long id;

    private String name;

    private Integer needSingleQuantity;

    private Integer purchaseSingleQuantity;

    private String singleUnit;

    private Integer needBundleQuantity;

    private Integer purchaseBundleQuantity;

    private String bundleUnit;

    private BigDecimal total;
}
