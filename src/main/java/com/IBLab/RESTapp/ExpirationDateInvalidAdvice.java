package com.IBLab.RESTapp;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

@ControllerAdvice
class ExpirationDateInvalidAdvice {

    @ResponseBody // advice rendered straight into responsebody
    @ExceptionHandler(ExpirationDateInvalidException.class) // triggers on this Exc.
    @ResponseStatus(HttpStatus.BAD_REQUEST) // == 400
    String expirationDateInvalidHandler(ExpirationDateInvalidException ex) {
        return ex.getMessage();
    }
}