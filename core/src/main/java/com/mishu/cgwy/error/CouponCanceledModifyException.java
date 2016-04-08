package com.mishu.cgwy.error;

/**
 * Created by king-ck on 2015/12/22.
 */
public class CouponCanceledModifyException extends RuntimeException {
    public CouponCanceledModifyException() {
    }

    public CouponCanceledModifyException(String message) {
        super(message);
    }

    public CouponCanceledModifyException(String message, Throwable cause) {
        super(message, cause);
    }

    public CouponCanceledModifyException(Throwable cause) {
        super(cause);
    }

    public CouponCanceledModifyException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
