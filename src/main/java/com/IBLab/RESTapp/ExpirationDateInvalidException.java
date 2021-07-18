package com.IBLab.RESTapp;

import java.time.LocalDate;

class ExpirationDateInvalidException extends RuntimeException {

    ExpirationDateInvalidException() {
        super("(HTTP ERR 400) Expiration date is not valid.");
        // Evokes a HTTP 400 by default by MVC config, see EmplyeeNotFoundAdvice.java
    }
}