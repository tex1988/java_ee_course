package com.gitlab.hillel.dnepr.java.ee.oleksii.zinkevych.utils;

import com.gitlab.hillel.dnepr.java.ee.oleksii.zinkevych.utils.exceptions.RepositoryUtilsException;
import lombok.extern.slf4j.Slf4j;

import java.util.Objects;
import java.util.UUID;

@Slf4j
public class CommonUtils<T, ID> {
    private final Class<T> clazz;

    public CommonUtils(Class<T> clazz) {
        this.clazz = clazz;
    }

    @SuppressWarnings("unchecked")
    public ID getFieldValueId(String fieldValue) {
        return (ID) UUID.nameUUIDFromBytes(fieldValue.getBytes()).toString();
    }

    public <S extends T> void validate(S entity) {
        Objects.requireNonNull(entity, "Entity is undefined");
        if (entity.getClass() != this.clazz) {
            throw new RepositoryUtilsException(new IllegalArgumentException("Wrong entity type"));
        }
    }
}
