package com.gitlab.hillel.dnepr.java.ee.oleksii.zinkevych.utils.exceptions;

import com.gitlab.hillel.dnepr.java.ee.common.repository.exception.UncheckedRepositoryException;


public class RepositoryUtilsSqlException extends UncheckedRepositoryException {
    public RepositoryUtilsSqlException() {
    }

    public RepositoryUtilsSqlException(String s) {
        super(s);
    }

    public RepositoryUtilsSqlException(String message, Throwable cause) {
        super(message, cause);
    }

    public RepositoryUtilsSqlException(Throwable cause) {
        super(cause);
    }
}