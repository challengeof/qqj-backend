package com.qqj.error;

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
public class BusinessRestExceptionHandler implements RestExceptionHandler<BusinessException, RestError> {
    @Override
    public ResponseEntity<RestError> handleException(BusinessException exception, HttpServletRequest request) {
        RestError restError = new RestError();
        restError.setErrno(exception.getErrorCode().getError());
        restError.setErrmsg(StringUtils.isNotBlank(exception.getErrMsg()) ? (exception.getErrMsg() + exception.getErrorCode().getErrorMessage()) : exception.getErrorCode().getErrorMessage());

        return new ResponseEntity<>(restError, HttpStatus.BAD_REQUEST);
    }
}
