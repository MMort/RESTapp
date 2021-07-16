package employee;

class EmployeeNotFoundException extends RuntimeException {

    EmployeeNotFoundException(Long id) {
        super("Could not find employee " + id);
        // Evokes a HTTP 404 by default by MVC config, see EmplyeeNotFoundAdvice.java
    }
}