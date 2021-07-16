package com.IBLab.RESTapp;

class ModuleNotFoundException extends RuntimeException {

    ModuleNotFoundException(Long id) {
        super("Module not found. id:" + id);
        // Evokes a HTTP 404 by default by MVC config, see ModuleNotFoundAdvice
    }
}