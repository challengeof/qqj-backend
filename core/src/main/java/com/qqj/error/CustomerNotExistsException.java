package com.qqj.error;

/**
 * Created by kaicheng on 3/18/15.
 */
public class CustomerNotExistsException extends BusinessException {
    public ErrorCode getErrorCode() {
        return ErrorCode.CustomerDoesNotExist;
    }
}
