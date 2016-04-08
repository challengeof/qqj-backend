package com.mishu.cgwy.error;

/**
 * Created by king-ck on 2016/3/8.
 */
public class RequestNotCorrectException extends BusinessException{
    @Override
    public ErrorCode getErrorCode() {
        return ErrorCode.RequestNotCorrect;
    }
}
