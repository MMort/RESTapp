package com.IBLab.RESTapp;

import java.time.LocalDate;

class ExpirationDateInvalidException extends RuntimeException {

    ExpirationDateInvalidException(LocalDate date) {
        super("Expiration date is not valid:" + date);
        // Evokes a HTTP 400 by default by MVC config, see EmplyeeNotFoundAdvice.java
    }
}