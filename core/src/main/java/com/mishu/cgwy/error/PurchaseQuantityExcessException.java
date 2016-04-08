package com.mishu.cgwy.error;

/**
 * Created by challenge on 16/1/7.
 */
public class PurchaseQuantityExcessException extends BusinessException {

    public PurchaseQuantityExcessException(String errMsg) {
        super.setErrMsg(errMsg);
    }

    @Override
    public ErrorCode getErrorCode() {
        return ErrorCode.PurchaseQuantityExcess;
    }
}
