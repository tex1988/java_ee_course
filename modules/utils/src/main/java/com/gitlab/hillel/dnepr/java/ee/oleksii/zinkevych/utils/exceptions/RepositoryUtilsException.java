package com.gitlab.hillel.dnepr.java.ee.oleksii.zinkevych.utils.exceptions;

public class RepositoryUtilsException extends IllegalStateException {
    public RepositoryUtilsException() {
    }

    public RepositoryUtilsException(String s) {
        super(s);
    }

    public RepositoryUtilsException(String message, Throwable cause) {
        super(message, cause);
    }

    public RepositoryUtilsException(Throwable cause) {
        super(cause);
    }
}
