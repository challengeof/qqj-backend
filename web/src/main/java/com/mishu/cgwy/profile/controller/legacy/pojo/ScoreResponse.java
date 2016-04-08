package com.mishu.cgwy.profile.controller.legacy.pojo;

import com.mishu.cgwy.error.RestError;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

/**
 * Created by kaicheng on 4/13/15.
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class ScoreResponse extends RestError {
    private BigDecimal score = new BigDecimal(0);
    private BigDecimal total = new BigDecimal(0);
    private BigDecimal back = new BigDecimal(0);
    private BigDecimal expect = new BigDecimal(0);
}
