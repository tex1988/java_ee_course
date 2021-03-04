package com.gitlab.hillel.dnepr.java.ee.common.validation;

import java.util.Objects;
import java.util.function.Consumer;

public interface Validator<T> {

    void validate(T input) throws ValidationException;

    default void validate(T input, Consumer<ValidationException> errorHandler) {
        Objects.requireNonNull(errorHandler, "Error handler is undefined");
        try {
            validate(input);
        } catch (ValidationException e) {
            errorHandler.accept(e);
        }
    }

    default boolean isValid(T input) {
        try {
            validate(input);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    class ValidationException extends Exception {
        public ValidationException() {
        }

        public ValidationException(String message) {
            super(message);
        }

        public ValidationException(String message, Throwable cause) {
            super(message, cause);
        }

        public ValidationException(Throwable cause) {
            super(cause);
        }
    }
}
