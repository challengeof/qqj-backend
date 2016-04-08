package com.mishu.cgwy.product.controller.legacy.pojo;

import com.mishu.cgwy.product.constants.Constants;
import lombok.Data;

/**
 * Created by kaicheng on 3/26/15.
 */
@Data
public class KeywordRequest {
    private String name;
    private Integer page = Constants.PAGE_DEFAULT;
    private Integer rows = Constants.ROWS_DEFAULT;

}
