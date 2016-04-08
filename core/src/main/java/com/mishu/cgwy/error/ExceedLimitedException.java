package com.mishu.cgwy.error;

/**
 * Created by challenge on 16/1/5.
 */
public class ExceedLimitedException extends BusinessException {
    @Override
    public ErrorCode getErrorCode() {
        return ErrorCode.ExceedLimited;
    }
}
