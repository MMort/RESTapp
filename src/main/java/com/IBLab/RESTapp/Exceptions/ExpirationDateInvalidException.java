package com.IBLab.RESTapp.Exceptions;

public class ExpirationDateInvalidException extends RuntimeException {

    public ExpirationDateInvalidException() {
        super("(HTTP ERR 400) Expiration date is not valid.");
        // Evokes a HTTP 400 by default by MVC config, see EmplyeeNotFoundAdvice.java
    }
}