package com.mishu.cgwy.product.wrapper;

import com.mishu.cgwy.product.domain.Brand;
import com.mishu.cgwy.utils.ValidStatus;
import lombok.Data;

import java.util.Date;

/**
 * User: xudong
 * Date: 3/6/15
 * Time: 4:30 PM
 */
@Data
@Deprecated
public class BrandWrapper {

    private Long id;
    private String brandName;
    private String status;
    private Date lastModified;

    public BrandWrapper() {
    }

    public BrandWrapper(Brand brand) {
        this.id = brand.getId();
        this.brandName = brand.getBrandName();
        this.status = ValidStatus.fromInt(brand.getStatus()).getName();
        this.lastModified = brand.getLastModified();
    }
}
