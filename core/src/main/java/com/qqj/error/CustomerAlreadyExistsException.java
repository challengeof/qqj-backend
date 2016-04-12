package com.qqj.error;

/**
 * User: xudong
 * Date: 2/28/15
 * Time: 6:09 PM
 */
public class CustomerAlreadyExistsException extends BusinessException {
    @Override
    public ErrorCode getErrorCode() {
        return ErrorCode.CustomerAlreadyExists;
    }
}
