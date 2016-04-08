package com.mishu.cgwy.error;

/**
 * Created by bowen on 15/11/18.
 */
public class ScoreExchangeException extends BusinessException {
    @Override
    public ErrorCode getErrorCode() {
        return ErrorCode.ScoreExchangeException;
    }
}
