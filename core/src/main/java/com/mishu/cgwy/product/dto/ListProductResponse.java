package com.mishu.cgwy.product.dto;

import com.mishu.cgwy.error.RestError;
import com.mishu.cgwy.product.wrapper.BrandWrapper;
import com.mishu.cgwy.product.wrapper.CategoryWrapper;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by kaicheng on 3/19/15.
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class ListProductResponse extends RestError {

    private Long total = 0L;
    private List<ProductItem> rows = new ArrayList<ProductItem>();
    private Set<BrandWrapper> brandList = new HashSet<BrandWrapper>();
    private List<CategoryWrapper> categoryList = new ArrayList<CategoryWrapper>();


}


