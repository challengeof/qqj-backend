package com.mishu.cgwy.score.controller;

import com.mishu.cgwy.order.dto.OrderStatistics;
import com.mishu.cgwy.order.wrapper.SimpleOrderWrapper;
import com.mishu.cgwy.score.Wrapper.ScoreLogWrapper;
import lombok.Data;

import java.util.List;

/**
 * Created by king-ck on 2015/11/12.
 */
@Data
public class ScoreLogPageResponse {

    private long total;
    private int page;
    private int pageSize;

    private List<ScoreLogWrapper> scoreLogs ;

}
