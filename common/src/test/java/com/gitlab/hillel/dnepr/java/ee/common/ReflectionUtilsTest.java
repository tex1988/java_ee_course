package com.gitlab.hillel.dnepr.java.ee.common;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.gitlab.hillel.dnepr.java.ee.common.utils.ReflectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.Test;

import java.util.UUID;

class ReflectionUtilsTest {
    @Test
    void getFieldValueWithWrongArgumentsTest() {
        assertThrows(NullPointerException.class,
            () -> ReflectionUtils.getFieldValue(null, StringUtils.EMPTY));
        assertThrows(IllegalArgumentException.class,
            () -> ReflectionUtils.getFieldValue(new TestClass(), null));
        assertThrows(IllegalArgumentException.class,
            () -> ReflectionUtils.getFieldValue(new TestClass(), StringUtils.EMPTY));
        assertThrows(NoSuchFieldException.class,
            () -> ReflectionUtils.getFieldValue(new TestClass(), "not_existed_key"));

        assertThrows(NullPointerException.class,
            () -> ReflectionUtils.getFieldValue(null, null, TestClass.class));
        assertThrows(IllegalArgumentException.class,
            () -> ReflectionUtils.getFieldValue(new TestClass(), null, TestClass.class));
        assertThrows(IllegalArgumentException.class,
            () -> ReflectionUtils.getFieldValue(new TestClass(), StringUtils.EMPTY, TestClass.class));
        assertThrows(NoSuchFieldException.class,
            () -> ReflectionUtils.getFieldValue(new TestClass(), "not_existed_key", TestClass.class));
    }

    @Test
    void getFieldValueWithValidArgumentsTest() {
        final TestClass testClass = new TestClass();
        testClass.key = UUID.randomUUID().toString();

        assertEquals(testClass.key, ReflectionUtils.getFieldValue(testClass, "key"));
    }

    @Test
    void getFieldValueWithNullClassTest() {
        final TestClass testClass = new TestClass();
        testClass.key = UUID.randomUUID().toString();

        assertThrows(NullPointerException.class,
            () -> ReflectionUtils.getFieldValue(testClass, "key", null));
    }

    @Test
    void setFieldValueWithWrongArgumentsTest() {
        final TestClass testClass = new TestClass();
        testClass.key = UUID.randomUUID().toString();
        final String newValue = UUID.randomUUID().toString();

        assertThrows(NullPointerException.class,
            () -> ReflectionUtils.setFieldValue(null, "key", newValue));
        assertThrows(IllegalArgumentException.class,
            () -> ReflectionUtils.setFieldValue(testClass, null, newValue));
        assertThrows(IllegalArgumentException.class,
            () -> ReflectionUtils.setFieldValue(testClass, StringUtils.EMPTY, newValue));
        assertThrows(NoSuchFieldException.class,
            () -> ReflectionUtils.setFieldValue(testClass, "not_existed_key", newValue));
    }

    @Test
    void setFieldValueTest() {
        final TestClass testClass = new TestClass();
        testClass.key = UUID.randomUUID().toString();
        final String newValue = UUID.randomUUID().toString();

        ReflectionUtils.setFieldValue(testClass, "key", newValue);

        assertEquals(newValue, testClass.key);
    }

    private static class TestClass {
        private String key;
    }
}