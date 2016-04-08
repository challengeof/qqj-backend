package com.mishu.cgwy.error;

/**
 * User: xudong
 * Date: 2/28/15
 * Time: 6:09 PM
 */
public class InvalidCouponException extends BusinessException {
    @Override
    public ErrorCode getErrorCode() {
        return ErrorCode.InvalidCoupon;
    }
}
