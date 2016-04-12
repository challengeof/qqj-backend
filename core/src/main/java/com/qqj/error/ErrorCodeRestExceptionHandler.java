package com.qqj.error;

import cz.jirutka.spring.exhandler.handlers.RestExceptionHandler;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import javax.servlet.http.HttpServletRequest;

/**
 * User: xudong
 * Date: 3/4/15
 * Time: 2:37 PM
 */
public class ErrorCodeRestExceptionHandler implements RestExceptionHandler<Exception, RestError> {
    private int error = 0;

    private HttpStatus httpStatus;

    @Override
    public ResponseEntity<RestError> handleException(Exception exception, HttpServletRequest request) {
        RestError restError = new RestError();
        restError.setErrno(error);

        return new ResponseEntity<RestError>(restError, httpStatus);
    }

    public void setError(int error) {
        this.error = error;
    }

    public void setHttpStatus(HttpStatus httpStatus) {
        this.httpStatus = httpStatus;
    }
}
