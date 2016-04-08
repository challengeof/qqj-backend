package com.mishu.cgwy.promotion.controller;

import com.mishu.cgwy.response.query.QueryResponse;
import lombok.Data;

/**
 * Created by king-ck on 2015/12/23.
 */
@Data
public class PromotionStatisticsResponse<T,Y> extends QueryResponse<T>{

    private Y lineTotal;

}
