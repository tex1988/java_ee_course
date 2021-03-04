package com.gitlab.hillel.dnepr.java.ee.oleksii.zinkevych.js_rest_repository.service.exception;

public class ServiceEntityException extends IllegalStateException {
    public ServiceEntityException() {
    }

    public ServiceEntityException(String s) {
        super(s);
    }

    public ServiceEntityException(String message, Throwable cause) {
        super(message, cause);
    }

    public ServiceEntityException(Throwable cause) {
        super(cause);
    }
}
