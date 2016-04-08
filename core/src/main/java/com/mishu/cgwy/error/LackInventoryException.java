package com.mishu.cgwy.error;

/**
 * User: admin
 * Date: 9/21/15
 * Time: 10:09 AM
 */
public class LackInventoryException extends BusinessException {

    public LackInventoryException(String errMsg) {
        super.setErrMsg(errMsg);
    }

    @Override
    public ErrorCode getErrorCode() {
        return ErrorCode.OutStock;
    }
}
