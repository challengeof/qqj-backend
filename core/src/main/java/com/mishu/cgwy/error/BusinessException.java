package com.mishu.cgwy.error;

import lombok.Data;

/**
 * User: xudong
 * Date: 3/4/15
 * Time: 2:34 PM
 */
@Data
public abstract class BusinessException extends RuntimeException {
    protected String errMsg = "";
    public abstract ErrorCode getErrorCode();


}
