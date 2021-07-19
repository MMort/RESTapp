package com.IBLab.RESTapp.Exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

@ControllerAdvice
class ModuleNotFoundAdvice {

    @ResponseBody // advice rendered straight into responsebody
    @ExceptionHandler(ModuleNotFoundException.class) // triggers on this Exc.
    @ResponseStatus(HttpStatus.NOT_FOUND) // == 404
    String ModuleNotFoundHandler(ModuleNotFoundException ex) {
        return ex.getMessage();
    }
}