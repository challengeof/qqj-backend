package com.mishu.cgwy.product.wrapper;

import com.mishu.cgwy.inventory.vo.VendorVo;
import com.mishu.cgwy.product.domain.Sku;
import com.mishu.cgwy.product.domain.SkuVendor;
import com.mishu.cgwy.profile.domain.Address;
import com.mishu.cgwy.profile.domain.Wgs84Point;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class SkuVendorWrapper {

    private Long id;

    private String city;

    private SimpleSkuWrapper sku;

    private VendorVo vendor;

    private BigDecimal fixedPrice;

    private BigDecimal singleSalePriceLimit;

    private BigDecimal bundleSalePriceLimit;

    public SkuVendorWrapper(Sku sku) {
        this.sku = new SimpleSkuWrapper(sku);
    }

    public SkuVendorWrapper(SkuVendor skuVendor) {
        this.id = skuVendor.getId();
        this.city = skuVendor.getCity().getName();
        this.sku = new SimpleSkuWrapper(skuVendor.getSku());
    }
}
