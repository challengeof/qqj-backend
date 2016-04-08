package com.mishu.cgwy.error;

/**
 * Created by wangwei on 15/10/14.
 */
public class OrderStatusHasChangedException extends BusinessException{
    @Override
    public ErrorCode getErrorCode() {
        return ErrorCode.OrderStatusHasChanged;
    }
}
