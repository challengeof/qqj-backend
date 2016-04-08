package com.mishu.cgwy.product.dto;

import com.mishu.cgwy.error.RestError;
import com.mishu.cgwy.product.wrapper.BrandWrapper;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by kaicheng on 3/23/15.
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class SearchProductResponse extends RestError {
    private Long total = 0L;
    private List<ProductItem> rows = new ArrayList<ProductItem>();
    private Set<BrandWrapper> brandList = new HashSet<BrandWrapper>();
}
