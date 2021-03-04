
package com.gitlab.hillel.dnepr.java.ee.common.repository.cqrs;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import com.gitlab.hillel.dnepr.java.ee.common.validation.Validator;
import com.gitlab.hillel.dnepr.java.ee.common.validation.Validator.ValidationException;
import org.junit.jupiter.api.Test;

import java.util.Objects;
import java.util.UUID;
import java.util.function.Consumer;

class ValidatorTest {
    private static final ValidationException EXPECTED_VALIDATION_EXCEPTION = new ValidationException();
    private static final Validator<String> TEST_INSTANCE = new TestValidatorImpl();

    @Test
    @SuppressWarnings("unchecked")
    void validationTest() {
        assertThrows(ValidationException.class, () -> TEST_INSTANCE.validate(null));
        assertTrue(TEST_INSTANCE.isValid(UUID.randomUUID().toString()));
        assertTrue(TEST_INSTANCE.isValid(""));
        assertFalse(TEST_INSTANCE.isValid(null));
        assertThrows(NullPointerException.class, () -> TEST_INSTANCE.validate("", null));
        final Consumer<ValidationException> exceptionConsumer = mock(Consumer.class);
        TEST_INSTANCE.validate(null, exceptionConsumer);
        verify(exceptionConsumer, times(1)).accept(EXPECTED_VALIDATION_EXCEPTION);
    }

    @Test
    void validationExceptionTest() {
        final Throwable expectedError = new NullPointerException();
        final String expectedErrorMessage = UUID.randomUUID().toString();

        ValidationException testInstance = new ValidationException();
        assertNull(testInstance.getCause());
        assertNull(testInstance.getMessage());

        testInstance = new ValidationException(expectedErrorMessage);
        assertNull(testInstance.getCause());
        assertEquals(expectedErrorMessage, testInstance.getMessage());

        testInstance = new ValidationException(expectedError);
        assertEquals(expectedError.getClass().getName(), testInstance.getMessage());
        assertSame(expectedError, testInstance.getCause());

        testInstance = new ValidationException(expectedErrorMessage, expectedError);
        assertSame(expectedError, testInstance.getCause());
        assertEquals(expectedErrorMessage, testInstance.getMessage());
    }

    private static class TestValidatorImpl implements Validator<String> {
        @Override
        public void validate(String input) throws ValidationException {
            if (Objects.isNull(input)) {
                throw EXPECTED_VALIDATION_EXCEPTION;
            }
        }
    }
}