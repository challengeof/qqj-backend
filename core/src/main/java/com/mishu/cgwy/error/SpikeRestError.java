package com.mishu.cgwy.error;

import com.mishu.cgwy.order.wrapper.CartSkuStockOutWrapper;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by king-ck on 2016/1/21.
 */
@Data
public class SpikeRestError extends RestError {

//    private ErrorCode errorCode;
    private List<CartSkuStockOutWrapper> stockOut = new ArrayList<>();

//    public SpikeRestError(ErrorCode errorCode, List<CartSkuStockOutWrapper> stockOut) {
//        this.setErrno(errorCode.getError());
//        this.setErrmsg(errorCode.getErrorMessage());
//        this.stockOut = stockOut;
//    }
}
