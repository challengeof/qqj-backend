package com.mishu.cgwy.product.dto;

import com.mishu.cgwy.error.RestError;
import com.mishu.cgwy.product.wrapper.CategoryWrapper;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by kaicheng on 3/23/15.
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class ChildCategoryResponse extends RestError {
    List<CategoryWrapper> categories = new ArrayList<CategoryWrapper>();

}
