package com.mishu.cgwy.error;

/**
 * Created by king-ck on 2016/1/15.
 */
public class SpikeInvalidException extends BusinessException {

    public ErrorCode errorCode;

    public SpikeInvalidException(ErrorCode errorCode,String errMsg) {
        this.errorCode = errorCode;
        this.errMsg=errMsg;
    }

    public SpikeInvalidException(ErrorCode errorCode) {
        this.errorCode = errorCode;
    }

    @Override
    public ErrorCode getErrorCode() {
        return errorCode;
    }
}
