package com.gitlab.hillel.dnepr.java.ee.oleksii.zinkevych.serializer.common;

public interface Serializer<T, ID> {
    byte[] serialize(T entity);

    T deserialize(byte[] bytes);
}
