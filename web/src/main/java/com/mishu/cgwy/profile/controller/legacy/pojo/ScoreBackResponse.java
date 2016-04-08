package com.mishu.cgwy.profile.controller.legacy.pojo;

import com.mishu.cgwy.error.RestError;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by kaicheng on 4/13/15.
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class ScoreBackResponse extends RestError {
    private List<BackItem> backList = new ArrayList<BackItem>();
}

@Data
class BackItem{
    private BigDecimal score = new BigDecimal(0);
    private BigDecimal amount = new BigDecimal(0);
    private String createTime = "";
    private Integer status = 0;
}
