package com.gitlab.hillel.dnepr.java.ee.oleksii.zinkevych.js_rest_repository.mapper;

public class MapperException extends RuntimeException {
    public MapperException() {
    }

    public MapperException(String message) {
        super(message);
    }

    public MapperException(String message, Throwable cause) {
        super(message, cause);
    }

    public MapperException(Throwable cause) {
        super(cause);
    }

    public MapperException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
