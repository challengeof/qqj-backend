package com.mishu.cgwy.product.wrapper;

import com.mishu.cgwy.product.domain.Sku;
import com.mishu.cgwy.utils.StringUtils;
import lombok.Data;
import java.util.HashMap;
import java.util.Map;

/**
 * User: xudong
 * Date: 3/5/15
 * Time: 11:08 PM
 */
@Data
public class CompleteSkuWrapper extends SkuWrapper {
    private Map<String, String> propertyMap = new HashMap<>();

    private String details = "";

    private String specification;//规格

    private String shelfLife;   //保质期


    public CompleteSkuWrapper() {
        super();

    }

    public CompleteSkuWrapper(Sku sku) {
        super(sku);

        if (sku.getProduct().getPropertyMap() != null) {
            propertyMap = sku.getProduct().getPropertyMap();
        }

        details = sku.getProduct().getDetails();
        this.specification = sku.getProduct().getSpecification();
        Integer shelfLifeNum = sku.getProduct().getShelfLife();
        this.shelfLife =  (shelfLifeNum != null && shelfLifeNum != 0) ? sku.getProduct().getShelfLife().toString().concat("天") : null;
    }


}
