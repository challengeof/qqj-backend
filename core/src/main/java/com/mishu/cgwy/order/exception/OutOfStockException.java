package com.mishu.cgwy.order.exception;

import com.mishu.cgwy.error.BusinessException;
import com.mishu.cgwy.error.ErrorCode;
import com.mishu.cgwy.product.vo.SkuVo;
import com.mishu.cgwy.order.wrapper.CartSkuStockOutWrapper;
import com.mishu.cgwy.product.wrapper.SkuWrapper;

import java.util.ArrayList;
import java.util.List;

/**
 * User: xudong
 * Date: 5/31/15
 * Time: 11:21 PM
 */
public class OutOfStockException extends BusinessException {
    private List<CartSkuStockOutWrapper> stockOut = new ArrayList<>();
    public OutOfStockException(List<CartSkuStockOutWrapper> stockOut) {
        this.stockOut = stockOut;
    }

    public List<CartSkuStockOutWrapper> getStockOut() {
        return stockOut;
    }

    @Override
    public ErrorCode getErrorCode() {
        return ErrorCode.OutStock;
    }
}
