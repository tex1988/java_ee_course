package com.gitlab.hillel.dnepr.java.ee.common.utils;

import lombok.SneakyThrows;
import org.apache.commons.lang3.StringUtils;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;

public final class ReflectionUtils {
    private static final String OBJECT_IS_UNDEFINED = "Object is undefined";
    private static final String FIELD_NAME_IS_UNDEFINED = "Field name is undefined";

    private static final Field modifiersField;

    static {
        try {
            modifiersField = Field.class.getDeclaredField("modifiers");
        } catch (NoSuchFieldException e) {
            throw new IllegalStateException("Failed to get 'modifiers' field", e);
        }
        modifiersField.setAccessible(true);
    }

    private ReflectionUtils() {
        // Utility class constructor
    }

    @SneakyThrows
    @SuppressWarnings("unchecked")
    public static <T> T getFieldValue(Object obj, String fieldName) {
        Objects.requireNonNull(obj, OBJECT_IS_UNDEFINED);
        if (StringUtils.isBlank(fieldName)) {
            throw new IllegalArgumentException(FIELD_NAME_IS_UNDEFINED);
        }
        Field declaredField = null;
        for (Class<?> clazz = obj.getClass(); clazz != null; clazz = clazz.getSuperclass()) {
            Optional<Field> fieldOptional = Stream
                    .of(clazz.getDeclaredFields())
                    .filter(field -> Objects.equals(field.getName(), fieldName))
                    .findFirst();
            if (fieldOptional.isPresent()) {
                declaredField = fieldOptional.get();
                declaredField.setAccessible(true);
                break;
            }
        }
        if (declaredField == null) {
            throw new NoSuchFieldException("Field not found. Field: " + fieldName);
        }
        modifiersField.setInt(declaredField, declaredField.getModifiers() & ~Modifier.FINAL);
        return (T) declaredField.get(obj);
    }

    @SneakyThrows
    public static <T> T getFieldValue(Object obj, String fieldName, Class<? extends T> clazz) {
        return clazz.cast(getFieldValue(obj, fieldName));
    }

    @SneakyThrows
    public static void setFieldValue(Object obj, String fieldName, Object fieldValue) {
        Objects.requireNonNull(obj, OBJECT_IS_UNDEFINED);
        if (StringUtils.isBlank(fieldName)) {
            throw new IllegalArgumentException(FIELD_NAME_IS_UNDEFINED);
        }
        Field declaredField = obj.getClass().getDeclaredField(fieldName);
        declaredField.setAccessible(true);
        modifiersField.setInt(declaredField, declaredField.getModifiers() & ~Modifier.FINAL);
        declaredField.set(obj, fieldValue);
    }
}
