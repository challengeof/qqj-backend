package com.mishu.cgwy.error;

import lombok.Data;

/**
 * User: xudong
 * Date: 3/3/15
 * Time: 2:35 PM
 */
@Data
public class RestError {
    private int errno = 0;
    private String errmsg = "";
}
