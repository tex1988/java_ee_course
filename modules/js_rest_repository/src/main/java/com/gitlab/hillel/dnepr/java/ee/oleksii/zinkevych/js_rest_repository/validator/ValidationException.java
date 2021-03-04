package com.gitlab.hillel.dnepr.java.ee.oleksii.zinkevych.js_rest_repository.validator;

public class ValidationException extends IllegalArgumentException {
    public ValidationException() {
    }

    public ValidationException(String s) {
        super(s);
    }

    public ValidationException(String message, Throwable cause) {
        super(message, cause);
    }

    public ValidationException(Throwable cause) {
        super(cause);
    }
}
