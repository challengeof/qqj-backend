package com.mishu.cgwy.error;

/**
 * Created by bowen on 15-7-7.
 */
public class RefundException extends BusinessException{
    @Override
    public ErrorCode getErrorCode() {
        return ErrorCode.InvalidRefundNo;
    }
}
