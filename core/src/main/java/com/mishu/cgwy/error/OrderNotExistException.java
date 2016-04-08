package com.mishu.cgwy.error;

/**
 * Created by wangwei on 15/10/14.
 */
public class OrderNotExistException  extends BusinessException{
    @Override
    public ErrorCode getErrorCode() {
        return ErrorCode.OrderNotExist;
    }
}
