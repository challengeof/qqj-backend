package com.mishu.cgwy.error;

import com.google.common.base.Function;
import com.google.common.collect.Collections2;
import com.mishu.cgwy.order.exception.OutOfStockException;
import com.mishu.cgwy.order.wrapper.CartSkuStockOutWrapper;
import com.mishu.cgwy.product.vo.SkuVo;
import cz.jirutka.spring.exhandler.handlers.RestExceptionHandler;
import org.apache.commons.lang.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import javax.servlet.http.HttpServletRequest;

/**
 * User: xudong
 * Date: 3/4/15
 * Time: 2:37 PM
 */
public class OutOfStockExceptionHandler implements RestExceptionHandler<OutOfStockException, RestError> {
    @Override
    public ResponseEntity<RestError> handleException(OutOfStockException exception, HttpServletRequest request) {
        RestError restError = new RestError();
        restError.setErrno(exception.getErrorCode().getError());

        String skuNames =StringUtils.join(Collections2.transform(exception.getStockOut(), new Function<CartSkuStockOutWrapper, String>() {
            @Override
            public String apply(CartSkuStockOutWrapper input) {
                return input.getSku().getName();
            }
        }), "，");


            restError.setErrmsg(skuNames+"缺货");

//        restError.setErrmsg(StringUtils.join(Collections2.transform(exception.getStockOut(), new Function<SkuWrapper, String>() {
//            @Override
//            public String apply(SkuWrapper input) {
//                return input.getName();
//            }
//        }), "，") + "缺货");

        return new ResponseEntity<>(restError, HttpStatus.BAD_REQUEST);
    }
}
