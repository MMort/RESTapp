package com.IBLab.RESTapp.Exceptions;

public class ModuleNotFoundException extends RuntimeException {

    public ModuleNotFoundException() {
        super("(HTTP ERR 404) Module not found.");
        // Evokes a HTTP 404 by default by MVC config, see ModuleNotFoundAdvice
    }
}