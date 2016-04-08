package com.mishu.cgwy.score.controller;

import lombok.Data;

import java.math.BigDecimal;

/**
 * Created by king-ck on 2015/11/16.
 */
@Data
public class ScoreSumResponse {

    private BigDecimal reciveAmount=new BigDecimal(0);
    private Long scoreSum=0L;
    
}
