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
public class ScoreComeResponse extends RestError {
    List<ComeItem> comeList = new ArrayList<ComeItem>();
}

@Data
class ComeItem{
    private Long rid = 0L;
    private String restaurantNumber = "";
    private String name = "";
    private BigDecimal price = new BigDecimal(0);

}
