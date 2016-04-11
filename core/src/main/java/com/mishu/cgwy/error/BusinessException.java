package com.mishu.cgwy.error;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public abstract class BusinessException extends RuntimeException {
    protected String errMsg = "";
    public abstract ErrorCode getErrorCode();


}
