package com.gitlab.hillel.dnepr.java.ee.common.repository.cqrs;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;

import static org.junit.jupiter.api.Assertions.assertThrows;

@Slf4j
public class ValidationTest {
    @Data
    static class User {
        @NotBlank
        String fName;
        @NotBlank
        String mName;
        @NotBlank
        String lName;
        @Max(25)
        @Min(10)
        int age;
    }

    @Test
    void test() {
        //TODO: Add validator implementation
        User user = new User()
                .setAge(23)
                .setFName("")
                .setMName("mN")
                .setLName("lN");
//        ValidationUtils.validate(user, throwable -> {
//            LOGGER.warn("Failed to validate", throwable);
//        });
//        assertThrows(ValidationException.class, () -> ValidationUtils.validate(user));
    }
}
