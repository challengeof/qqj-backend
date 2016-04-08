package com.mishu.cgwy.error;

import com.mishu.cgwy.order.exception.SpikeOutOfStockException;
import cz.jirutka.spring.exhandler.handlers.RestExceptionHandler;
import org.apache.commons.lang.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import javax.servlet.http.HttpServletRequest;

/**
 * Created by king-ck on 2016/1/21.
 */
public class SpikeOutOfStockExceptionHandler implements RestExceptionHandler<SpikeOutOfStockException, SpikeRestError> {
    @Override
    public ResponseEntity<SpikeRestError> handleException(SpikeOutOfStockException exception, HttpServletRequest request) {
        SpikeRestError restError = new SpikeRestError();

        restError.setErrno(exception.getErrorCode().getError());
        restError.setErrmsg(exception.getErrorCode().getErrorMessage());
        restError.setStockOut(exception.getStockOut());

        return new ResponseEntity<>(restError, HttpStatus.BAD_REQUEST);

    }
}
