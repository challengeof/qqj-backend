package com.mishu.cgwy.error;

/**
 * User: xudong
 * Date: 6/10/15
 * Time: 5:42 PM
 */

public class TooMuchCodeRetryException extends BusinessException {
    @Override
    public ErrorCode getErrorCode() {
        return ErrorCode.TooMuchCodeRetry;
    }
}

