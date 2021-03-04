package com.gitlab.hillel.dnepr.java.ee.common.repository.exception;

public class CheckedRepositoryException extends Exception {
    public CheckedRepositoryException() {
    }

    public CheckedRepositoryException(String message) {
        super(message);
    }

    public CheckedRepositoryException(String message, Throwable cause) {
        super(message, cause);
    }

    public CheckedRepositoryException(Throwable cause) {
        super(cause);
    }

    public CheckedRepositoryException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
