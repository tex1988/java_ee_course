package com.gitlab.hillel.dnepr.java.ee.common.validation;

import org.apache.commons.lang3.StringUtils;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.ValidationException;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.util.Objects;
import java.util.Set;
import java.util.function.Consumer;

public final class ValidationUtils {
    private static final ValidatorFactory VALIDATOR_FACTORY = Validation.buildDefaultValidatorFactory();
    private static final Validator VALIDATOR = VALIDATOR_FACTORY.getValidator();

    private ValidationUtils() {
    }

    public static void validate(Object object, Consumer<Throwable> errorConsumer) {
        final Set<ConstraintViolation<Object>> constraintViolations = VALIDATOR.validate(object);
        if (!constraintViolations.isEmpty()) {
            final StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append(String.format("%sError count: %s;%s", StringUtils.LF, constraintViolations.size(), StringUtils.LF));
            for (ConstraintViolation<Object> constraintViolation : constraintViolations) {
                stringBuilder
                        .append(String.format("Field: %s; ", constraintViolation.getPropertyPath()))
                        .append(String.format("Value: %s; ", constraintViolation.getInvalidValue()))
                        .append(String.format("Message: %s; ", constraintViolation.getMessage()))
                        .append(StringUtils.LF);
            }
            final ValidationException error = new ValidationException(stringBuilder.toString());
            if (Objects.isNull(errorConsumer)) {
                throw error;
            }
            errorConsumer.accept(error);
        }
    }

    public static void validate(Object object) {
        validate(object, null);
    }
}
