package com.gitlab.hillel.dnepr.java.ee.common.serialization;

public interface StringSerializer {
    <T> String toString(T object);

    <T> T fromString(String string, Class<T> objectType);
}
