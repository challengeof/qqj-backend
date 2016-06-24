package com.qqj.product.controller.wrapper;

import com.qqj.product.controller.domain.Product;
import com.qqj.product.controller.enumeration.ProductStatus;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

/**
 * Created by wangguodong on 16/4/12.
 */
@Setter
@Getter
public class ProductWrapper {
    private Long id;

    private String name;

    private Short status;

    private String statusName;

    private BigDecimal price;

    private BigDecimal price0;

    private BigDecimal price1;

    private BigDecimal price2;

    public ProductWrapper(Product product) {
        this.id = product.getId();
        this.name = product.getName();
        this.status = product.getStatus();
        this.statusName = ProductStatus.get(product.getStatus()).getName();
        this.price = product.getPrice();
        this.price0 = product.getPrice0();
        this.price1 = product.getPrice1();
        this.price2 = product.getPrice2();
    }
}
