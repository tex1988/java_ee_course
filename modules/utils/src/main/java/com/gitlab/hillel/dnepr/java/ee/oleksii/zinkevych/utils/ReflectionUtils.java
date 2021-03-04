package com.gitlab.hillel.dnepr.java.ee.oleksii.zinkevych.utils;

import com.gitlab.hillel.dnepr.java.ee.oleksii.zinkevych.utils.exceptions.RepositoryUtilsException;
import lombok.extern.slf4j.Slf4j;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
public class ReflectionUtils<T> {
    private final Class<T> clazz;
    private final Map<String, setFieldTypeFunction<T>> setFieldTypeFunctionMap = new HashMap<>();
    private final Map<String, getFieldTypeFunction<T>> getFieldTypeFunctionMap = new HashMap<>();

    public ReflectionUtils(Class<T> clazz) {
        this.clazz = clazz;
        addSetFieldTypeFunctions();
        addGetFieldTypeFunctions();
    }

    @FunctionalInterface
    interface setFieldTypeFunction<T> {
        default void setFieldValue(Field field, T entity, String value) {
            try {
                apply(field, entity, value);
            } catch (IllegalAccessException e) {
                RepositoryUtilsException exception = new RepositoryUtilsException(e);
                LOGGER.error("Exception: ", exception);
                throw exception;
            }
        }

        void apply(Field field, T entity, String value) throws IllegalAccessException;
    }

    @FunctionalInterface
    interface getFieldTypeFunction<T> {
        default String getFieldValue(Field field, T entity) {
            try {
                return apply(field, entity);
            } catch (IllegalAccessException e) {
                RepositoryUtilsException exception = new RepositoryUtilsException(e);
                LOGGER.error("Exception: ", exception);
                throw exception;
            }
        }

        String apply(Field field, T entity) throws IllegalAccessException;
    }

    private void addSetFieldTypeFunctions() {
        setFieldTypeFunctionMap.put("String", Field::set);
        setFieldTypeFunctionMap.put("id", Field::set);
        setFieldTypeFunctionMap.put("int", (field, entity, value) -> field.set(entity, Integer.valueOf(value)));
        setFieldTypeFunctionMap.put("double", (field, entity, value) -> field.set(entity, Double.valueOf(value)));
        setFieldTypeFunctionMap.put("long", (field, entity, value) -> field.set(entity, Long.valueOf(value)));
        setFieldTypeFunctionMap.put("float", (field, entity, value) -> field.set(entity, Float.valueOf(value)));
        setFieldTypeFunctionMap.put("boolean", (field, entity, value) -> field.set(entity, Boolean.valueOf(value)));
    }

    private void addGetFieldTypeFunctions() {
        getFieldTypeFunctionMap.put("String", (field, entity) -> String.valueOf(field.get(entity)));
        getFieldTypeFunctionMap.put("id", (field, entity) -> String.valueOf(field.get(entity)));
        getFieldTypeFunctionMap.put("int", (field, entity) -> String.valueOf(field.get(entity)));
        getFieldTypeFunctionMap.put("double", (field, entity) -> String.valueOf(field.get(entity)));
        getFieldTypeFunctionMap.put("long", (field, entity) -> String.valueOf(field.get(entity)));
        getFieldTypeFunctionMap.put("float", (field, entity) -> String.valueOf(field.get(entity)));
        getFieldTypeFunctionMap.put("boolean", (field, entity) -> String.valueOf(field.get(entity)));
    }

    public static List<Field> addClassFields(Class<?> clazz) {
        List<Field> result = new ArrayList<>();
        for (Field field : clazz.getDeclaredFields()) {
            field.setAccessible(true);
            result.add(field);
        }
        for (Field field : clazz.getSuperclass().getDeclaredFields()) {
            field.setAccessible(true);
            result.add(field);
        }
        return result;
    }

    public T createEntity() {
        T result;
        try {
            result = this.clazz.getDeclaredConstructor().newInstance();
        } catch (InstantiationException |
                IllegalAccessException |
                InvocationTargetException |
                NoSuchMethodException e) {
            LOGGER.error("Exception: ", e);
            throw new RepositoryUtilsException("Failed to create entity", e);
        }
        return result;
    }

    void setFieldValue(Field field, T entity, String value) {
        String fieldType = field.getType().getSimpleName();
        if (setFieldTypeFunctionMap.containsKey(fieldType)) {
            setFieldTypeFunctionMap
                    .get(fieldType)
                    .setFieldValue(field, entity, value);
        } else {
            RepositoryUtilsException exception = new RepositoryUtilsException("Unknown type");
            LOGGER.error("Exception: ", exception);
            throw exception;
        }
    }

    public String getFieldValue(Field field, T entity) {
        String result;
        String fieldType = field.getType().getSimpleName();
        if (getFieldTypeFunctionMap.containsKey(fieldType)) {
            result = getFieldTypeFunctionMap
                    .get(fieldType)
                    .getFieldValue(field, entity);
        } else {
            RepositoryUtilsException exception = new RepositoryUtilsException("Unknown type");
            LOGGER.error("Exception: ", exception);
            throw exception;
        }
        return result;
    }

    public static Field getAnnotatedField(Class<?> entityClass, Class<? extends Annotation> annotationClass) {
        Field result;
        result = getAnnotatedFieldFromClass(entityClass, annotationClass);
        if (result != null) return result;

        result = getAnnotatedFieldFromClass(entityClass.getSuperclass(), annotationClass);
        if (result != null) return result;

        result = getFieldByAnnotatedGetter(entityClass, annotationClass);
        if (result != null) return result;

        result = getFieldByAnnotatedGetter(entityClass.getSuperclass(), annotationClass);
        if (result != null) return result;

        RepositoryUtilsException exception =
                new RepositoryUtilsException(
                        new NoSuchFieldException(
                                "Field with" + annotationClass.getName() + " in " + entityClass.getName() + " is missing"));
        LOGGER.error("Exception: ", exception);
        throw exception;
    }

    public static Field getAnnotatedField(Class<?> entityClass,
                                          Class<? extends Annotation> annotationClass,
                                          String key,
                                          String value) {
        Field result;
        result = getAnnotatedFieldFromClass(entityClass, annotationClass, key, value);
        if (result != null) return result;

        result = getAnnotatedFieldFromClass(entityClass.getSuperclass(), annotationClass, key, value);
        if (result != null) return result;

        result = getFieldByAnnotatedGetter(entityClass, annotationClass, key, value);
        if (result != null) return result;

        result = getFieldByAnnotatedGetter(entityClass.getSuperclass(), annotationClass, key, value);
        if (result != null) return result;

        RepositoryUtilsException exception =
                new RepositoryUtilsException(
                        new NoSuchFieldException(
                                "Field with annotation @" + annotationClass.getSimpleName() +
                                        " (attribute: " + key + "='" + value + "') in " +
                                        entityClass.getSimpleName() + ".class is missing"));
        LOGGER.error("Exception: ", exception);
        throw exception;
    }

    private static Field getAnnotatedFieldFromClass(Class<?> entityClass, Class<? extends Annotation> annotationClass) {
        Field result = null;
        Field[] fields = entityClass.getDeclaredFields();
        for (Field field : fields) {
            if (field.isAnnotationPresent(annotationClass)) {
                result = field;
                break;
            }
        }
        return result;
    }

    private static Field getAnnotatedFieldFromClass(Class<?> entityClass,
                                                    Class<? extends Annotation> annotationClass,
                                                    String key,
                                                    String value) {
        Field result = null;
        Field[] fields = entityClass.getDeclaredFields();
        for (Field field : fields) {
            if (field.isAnnotationPresent(annotationClass)) {
                Annotation annotation = field.getAnnotation(annotationClass);
                if (isAnnotationTypeMatch(annotation, key, value)) {
                    result = field;
                    break;
                }
            }
        }
        return result;
    }


    private static Field getFieldByAnnotatedGetter(Class<?> entityClass, Class<? extends Annotation> annotationClass) {
        Field result = null;
        Method[] methods = entityClass.getDeclaredMethods();
        for (Method method : methods) {
            if (method.isAnnotationPresent(annotationClass)) {
                result = getFieldByGetter(entityClass, method);
            }
        }
        return result;
    }

    private static Field getFieldByAnnotatedGetter(Class<?> entityClass,
                                                   Class<? extends Annotation> annotationClass,
                                                   String key,
                                                   String value) {
        Field result;
        Method fieldGetterMethod = null;
        Method[] methods = entityClass.getDeclaredMethods();
        for (Method method : methods) {
            if (method.isAnnotationPresent(annotationClass)) {
                Annotation annotation = method.getAnnotation(annotationClass);
                if (isAnnotationTypeMatch(annotation, key, value)) {
                    fieldGetterMethod = method;
                    break;
                }
            }
        }
        if (fieldGetterMethod == null) {
            return null;
        }
        result = getFieldByGetter(entityClass, fieldGetterMethod);
        return result;
    }

    private static boolean isAnnotationTypeMatch(Annotation annotation, String key, String value) {
        boolean result = false;
        Class<? extends Annotation> type = annotation.annotationType();
        for (Method annotationAttribute : type.getDeclaredMethods()) {
            if (annotationAttribute.getName().equals(key)) {
                try {
                    Object attributeValue = annotationAttribute.invoke(annotation, (Object[]) null);
                    if (attributeValue.equals(value)) {
                        result = true;
                        break;
                    }
                } catch (IllegalAccessException | InvocationTargetException e) {
                    RepositoryUtilsException exception = new RepositoryUtilsException(e);
                    LOGGER.error("Exception", e);
                    throw exception;
                }
            }
        }
        return result;
    }

    private static Field getFieldByGetter(Class<?> entityClass, Method fieldGetter) {
        Field result;
        String fieldGetterName = fieldGetter.getName();
        String fieldName;
        try {
            fieldName = fieldGetterName.split("get")[1].toLowerCase();
        } catch (ArrayIndexOutOfBoundsException e) {
            RepositoryUtilsException exception =
                    new RepositoryUtilsException("Getter " +
                            fieldGetter.getName() +
                            " has invalid name ", e);
            LOGGER.error("Exception: ", exception);
            throw exception;
        }
        try {
            result = entityClass.getDeclaredField(fieldName);
        } catch (NoSuchFieldException e) {
            RepositoryUtilsException exception =
                    new RepositoryUtilsException("Getter name (" + fieldGetterName + ") and field name (" +
                            fieldName + ") are mismatch in " +
                            entityClass.getName(), e);
            LOGGER.error("Exception: ", exception);
            throw exception;
        }
        return result;
    }
}