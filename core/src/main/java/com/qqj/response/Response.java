package com.qqj.response;

import lombok.Data;

@Data
public class Response<T> {
    protected boolean success = Boolean.TRUE;

    protected String msg;
}
