package com.gitlab.hillel.dnepr.java.ee.common.repository.exception;

public class UncheckedRepositoryException extends IllegalStateException {
    public UncheckedRepositoryException() {
    }

    public UncheckedRepositoryException(String s) {
        super(s);
    }

    public UncheckedRepositoryException(String message, Throwable cause) {
        super(message, cause);
    }

    public UncheckedRepositoryException(Throwable cause) {
        super(cause);
    }
}
