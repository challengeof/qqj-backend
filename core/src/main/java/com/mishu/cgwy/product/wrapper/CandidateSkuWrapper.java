package com.mishu.cgwy.product.wrapper;

import com.mishu.cgwy.product.domain.Sku;
import lombok.Data;

@Data
public class CandidateSkuWrapper {

    private Long id;

    private String name;

    public CandidateSkuWrapper(Sku sku) {
        this.id = sku.getId();
        this.name = sku.getName();
    }
}
