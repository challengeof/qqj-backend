package com.mishu.cgwy.error;

import org.apache.commons.lang.StringUtils;

import java.util.Collection;

/**
 * Created by king-ck on 2016/3/16.
 */
public class CustomerAuditExistsException extends BusinessException {
    public ErrorCode getErrorCode() {
        return ErrorCode.CustomerAuditExists;
    }


    public CustomerAuditExistsException() {
    }
    public CustomerAuditExistsException(String msg) {
        this.errMsg=msg;
    }

    public CustomerAuditExistsException(Collection<Long> restaurantIds) {
        this.errMsg= this.getErrorCode().getErrorMessage()+" 餐馆Id:" + StringUtils.join(restaurantIds, ",");
    }
}
