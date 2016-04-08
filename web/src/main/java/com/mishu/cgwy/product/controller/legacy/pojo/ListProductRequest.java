package com.mishu.cgwy.product.controller.legacy.pojo;

import com.mishu.cgwy.product.constants.Constants;
import lombok.Data;

/**
 * Created by kaicheng on 3/19/15.
 */
@Data
public class ListProductRequest {
    private Long categoryId;
    private Long brandId;
    private String sort = Constants.SORT_SELL_COUNT;
    private String order = Constants.ORDER_DESC;
    private Integer page = null;
    private Integer rows = null;
}
