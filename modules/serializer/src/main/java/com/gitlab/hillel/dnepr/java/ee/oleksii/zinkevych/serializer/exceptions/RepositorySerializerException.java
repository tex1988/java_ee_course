package com.gitlab.hillel.dnepr.java.ee.oleksii.zinkevych.serializer.exceptions;

public class RepositorySerializerException extends IllegalStateException {
    public RepositorySerializerException() {
    }

    public RepositorySerializerException(String s) {
        super(s);
    }

    public RepositorySerializerException(String message, Throwable cause) {
        super(message, cause);
    }

    public RepositorySerializerException(Throwable cause) {
        super(cause);
    }
}
