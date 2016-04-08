package com.mishu.cgwy.error;

/**
 * User: xudong
 * Date: 2/28/15
 * Time: 6:09 PM
 */
public class RestaurantMoreThanOneException extends BusinessException {
    @Override
    public ErrorCode getErrorCode() {
        return ErrorCode.RestaurantMoreThanOne;
    }
}
