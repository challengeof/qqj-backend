package com.mishu.cgwy.order.dto;

import com.mishu.cgwy.error.RestError;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by bowen on 15-4-30.
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class RiskOrderResponse extends RestError{

    private List risk = new ArrayList();

    private List repeat = new ArrayList();
}
