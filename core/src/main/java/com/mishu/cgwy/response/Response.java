package com.mishu.cgwy.response;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class Response<T> {
    protected boolean success;

    protected String msg;
}
