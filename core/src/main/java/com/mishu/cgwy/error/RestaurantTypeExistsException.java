package com.mishu.cgwy.error;

/**
 * Created by bowen on 16/1/27.
 */
public class RestaurantTypeExistsException extends BusinessException {
    @Override
    public ErrorCode getErrorCode() {
        return ErrorCode.RestaurantTypeExists;
    }
}
