package com.mishu.cgwy.search;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * User: xudong
 * Date: 3/11/15
 * Time: 5:17 PM
 */
@Data
public class SkuSearchHits {
    private Long totalResults = 0l;
    private List<Long> skuIds = new ArrayList<Long>();
}
