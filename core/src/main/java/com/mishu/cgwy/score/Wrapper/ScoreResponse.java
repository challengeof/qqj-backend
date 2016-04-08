package com.mishu.cgwy.score.Wrapper;

import lombok.Data;

/**
 * Created by bowen on 15/11/10.
 */
@Data
public class ScoreResponse {

    private Long totalScore;

    private Long exchangeScore;

    private Long availableScore;

    private Long lastMonthObtainScore;
}
