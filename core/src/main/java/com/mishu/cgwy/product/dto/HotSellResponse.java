package com.mishu.cgwy.product.dto;

import com.mishu.cgwy.error.RestError;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by kaicheng on 3/26/15.
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class HotSellResponse extends RestError {
    List<ProductItem> hotSellList = new ArrayList<ProductItem>();
}
