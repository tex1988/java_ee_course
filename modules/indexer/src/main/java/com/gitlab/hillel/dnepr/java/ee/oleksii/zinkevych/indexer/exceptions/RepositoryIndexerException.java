package com.gitlab.hillel.dnepr.java.ee.oleksii.zinkevych.indexer.exceptions;

public class RepositoryIndexerException extends IllegalStateException {
    public RepositoryIndexerException() {
    }

    public RepositoryIndexerException(String s) {
        super(s);
    }

    public RepositoryIndexerException(String message, Throwable cause) {
        super(message, cause);
    }

    public RepositoryIndexerException(Throwable cause) {
        super(cause);
    }
}
