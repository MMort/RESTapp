package com.IBLab.RESTapp;

class ModuleNotFoundException extends RuntimeException {

    ModuleNotFoundException() {
        super("(HTTP ERR 404) Module not found.");
        // Evokes a HTTP 404 by default by MVC config, see ModuleNotFoundAdvice
    }
}