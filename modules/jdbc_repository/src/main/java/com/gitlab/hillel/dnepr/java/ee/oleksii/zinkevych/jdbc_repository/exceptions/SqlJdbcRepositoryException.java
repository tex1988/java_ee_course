package com.gitlab.hillel.dnepr.java.ee.oleksii.zinkevych.jdbc_repository.exceptions;


import com.gitlab.hillel.dnepr.java.ee.common.repository.exception.UncheckedRepositoryException;

public class SqlJdbcRepositoryException extends UncheckedRepositoryException {
    public SqlJdbcRepositoryException() {
    }

    public SqlJdbcRepositoryException(String s) {
        super(s);
    }

    public SqlJdbcRepositoryException(String message, Throwable cause) {
        super(message, cause);
    }

    public SqlJdbcRepositoryException(Throwable cause) {
        super(cause);
    }
}
