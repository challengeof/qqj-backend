package com.mishu.cgwy.product.wrapper;

import com.mishu.cgwy.common.wrapper.MediaFileWrapper;
import com.mishu.cgwy.product.domain.Product;
import com.mishu.cgwy.product.domain.Sku;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * User: xudong
 * Date: 3/5/15
 * Time: 11:00 PM
 */
@Deprecated
@Data
public class SimpleProductWrapper {
    private Long id;

    private String name;

    private BrandWrapper brand;

    private CategoryWrapper category;

//    private String description;

    private int capacityInBundle = 1;

    private String barCode;

    private Integer shelfLife;

    private String specification;

    public SimpleProductWrapper() {

    }

    public SimpleProductWrapper(Product product) {
        this.id = product.getId();
        this.name = product.getName();
        this.brand = product.getBrand() == null ? null : new BrandWrapper(product.getBrand());
        this.category = product.getCategory() == null ? null : new CategoryWrapper(product.getCategory());
//        this.description = product.getDescription();
        this.shelfLife = product.getShelfLife();
        this.specification = product.getSpecification();

//        this.capacityInBundle = product.getCapacityInBundle();

        this.barCode = product.getBarCode();

    }
}
