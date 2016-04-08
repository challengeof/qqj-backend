package com.mishu.cgwy.product.controller.legacy.pojo;

import com.mishu.cgwy.product.constants.Constants;
import lombok.Data;


/**
 * Created by kaicheng on 3/23/15.
 */
@Data
public class SearchProductRequest {
    private Long brandId;
    private String sort = Constants.SORT_SELL_COUNT;
    private String order = Constants.ORDER_DESC;
    private Integer page = Constants.PAGE_DEFAULT;
    private Integer rows = Constants.ROWS_DEFAULT;
    private String name;
}
