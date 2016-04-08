package com.mishu.cgwy.stock.dto;

import com.mishu.cgwy.stock.wrapper.StockWrapper;
import lombok.Data;

import java.util.List;

/**
 * User: xudong
 * Date: 9/14/15
 * Time: 12:20 PM
 */
@Data
public class StockQueryResponse {

    private long total;
    private int page;
    private int pageSize;

    private List<StockWrapper> stocks;

    private StockStatistics stockStatistics;
}
