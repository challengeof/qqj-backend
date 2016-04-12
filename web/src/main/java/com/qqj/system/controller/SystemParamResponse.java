package com.qqj.system.controller;

import com.qqj.error.RestError;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * User: xudong
 * Date: 5/13/15
 * Time: 4:46 PM
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class SystemParamResponse extends RestError {
    private String shippingFee = "0";
    private String shippingFeeLimit = "1";
    private String orderSyncTime = "23";
}
