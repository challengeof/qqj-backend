package com.mishu.cgwy.score.controller;

import com.mishu.cgwy.score.Wrapper.ScoreLogWrapper;
import com.mishu.cgwy.score.Wrapper.ScoreWrapper;
import lombok.Data;

import java.util.List;

/**
 * Created by king-ck on 2015/11/12.
 */
@Data
public class ScoreQueryResponse {

    private long total;
    private int page;
    private int pageSize;

    private List<ScoreWrapper> scores;

}
