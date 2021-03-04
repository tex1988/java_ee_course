package com.gitlab.hillel.dnepr.java.ee.oleksii.zinkevych.js_rest_repository.validator;

public class AddressValidationException extends ValidationException {
    public AddressValidationException() {
    }

    public AddressValidationException(String s) {
        super(s);
    }

    public AddressValidationException(String message, Throwable cause) {
        super(message, cause);
    }

    public AddressValidationException(Throwable cause) {
        super(cause);
    }
}
