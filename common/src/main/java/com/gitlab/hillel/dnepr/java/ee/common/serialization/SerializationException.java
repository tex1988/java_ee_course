package com.gitlab.hillel.dnepr.java.ee.common.serialization;

public class SerializationException extends IllegalStateException {
    public SerializationException() {
    }

    public SerializationException(String s) {
        super(s);
    }

    public SerializationException(String message, Throwable cause) {
        super(message, cause);
    }

    public SerializationException(Throwable cause) {
        super(cause);
    }
}
