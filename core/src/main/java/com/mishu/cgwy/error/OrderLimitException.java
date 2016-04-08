package com.mishu.cgwy.error;

/**
 * User: guodong
 */
public class OrderLimitException extends BusinessException {

    public OrderLimitException(String errMsg) {
        super.setErrMsg(errMsg);
    }

    @Override
    public ErrorCode getErrorCode() {
        return ErrorCode.OrderLimit;
    }
}
