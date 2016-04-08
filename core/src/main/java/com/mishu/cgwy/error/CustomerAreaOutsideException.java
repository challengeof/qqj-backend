package com.mishu.cgwy.error;

/**
 * Created by king-ck on 2015/10/29.
 */
public class CustomerAreaOutsideException extends BusinessException {

    @Override
    public ErrorCode getErrorCode() {
        return ErrorCode.CustomerAreaOutside;
    }
}
