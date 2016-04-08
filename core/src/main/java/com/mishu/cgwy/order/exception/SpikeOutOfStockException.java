package com.mishu.cgwy.order.exception;

import com.mishu.cgwy.error.BusinessException;
import com.mishu.cgwy.error.ErrorCode;
import com.mishu.cgwy.order.wrapper.CartSkuStockOutWrapper;
import lombok.Data;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by king-ck on 2016/1/13.
 */

public class SpikeOutOfStockException  extends BusinessException {

    private ErrorCode errorCode;
    private List<CartSkuStockOutWrapper> stockOut = new ArrayList<>();

    public SpikeOutOfStockException(ErrorCode errorCode,List<CartSkuStockOutWrapper> stockOut) {
        this.stockOut=stockOut;
        this.errorCode=errorCode;
    }

//    @Override
//    public ErrorCode getErrorCode() {
//        return this.errorCode;
//    }


    public ErrorCode getErrorCode() {
        return errorCode;
    }


    public List<CartSkuStockOutWrapper> getStockOut() {
        return stockOut;
    }
}
