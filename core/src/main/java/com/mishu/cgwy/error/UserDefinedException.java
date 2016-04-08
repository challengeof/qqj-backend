package com.mishu.cgwy.error;

/**
 * User: admin
 * Date: 10/8/15
 * Time: 10:09 AM
 */
public class UserDefinedException extends BusinessException {

    public UserDefinedException(String errMsg) {
        super.setErrMsg(errMsg);
    }

    @Override
    public ErrorCode getErrorCode() {
        return ErrorCode.UserDefined;
    }
}
