package com.qqj.error;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public abstract class BusinessException extends RuntimeException {
    protected String errMsg = "";
    public abstract ErrorCode getErrorCode();


}
