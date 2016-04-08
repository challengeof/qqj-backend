package com.mishu.cgwy.product.dto;

import com.mishu.cgwy.error.RestError;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by kaicheng on 3/23/15.
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class GetProductResponse extends RestError {
    private Long productId;
    private String productNumber;
    private Long categoryId;
    private String name;
    private BigDecimal price;
    private BigDecimal marketPrice;
    private Integer maxBuy;
    private Long richTextId;
    List<String> imageList = new ArrayList<String>();
    List<MetadataItem> metaDataList = new ArrayList<MetadataItem>();


}


