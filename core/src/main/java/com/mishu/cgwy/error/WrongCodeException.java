package com.mishu.cgwy.error;

/**
 * User: xudong
 * Date: 6/10/15
 * Time: 5:42 PM
 */

public class WrongCodeException extends BusinessException {
    @Override
    public ErrorCode getErrorCode() {
        return ErrorCode.WrongCode;
    }
}

