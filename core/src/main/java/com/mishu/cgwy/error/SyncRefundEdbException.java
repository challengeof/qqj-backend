package com.mishu.cgwy.error;

/**
 * Created by bowen on 15/8/7.
 */
public class SyncRefundEdbException extends BusinessException{
    @Override
    public ErrorCode getErrorCode() {
        return ErrorCode.InvalidRefundNo;
    }
}
