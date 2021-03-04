package com.gitlab.hillel.dnepr.java.ee.oleksii.zinkevych.js_rest_repository.validator;

import org.springframework.stereotype.Component;

@Component
public class AddressValidator implements Validator {
    @Override
    public void validate(String expression) throws ValidationException {
        String[] stringArr = expression.split(",");
        if (stringArr.length < 4) {
            throw new AddressValidationException("Invalid address format");
        }
    }
}