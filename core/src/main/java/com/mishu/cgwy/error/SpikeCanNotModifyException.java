package com.mishu.cgwy.error;
/**
 * Created by king-ck on 2016/4/5.
 */
public class SpikeCanNotModifyException extends BusinessException {
    public ErrorCode errorCode;

    @Override
    public ErrorCode getErrorCode() {
        return ErrorCode.SpikeCanNotModify;
    }


}
