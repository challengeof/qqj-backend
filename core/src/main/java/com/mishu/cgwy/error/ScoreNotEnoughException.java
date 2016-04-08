package com.mishu.cgwy.error;

/**
 * Created by king-ck on 2016/1/20.
 */
public class ScoreNotEnoughException extends BusinessException{
    @Override
    public ErrorCode getErrorCode() {
        return ErrorCode.ScoreNotEnough;
    }
}
