package com.gitlab.hillel.dnepr.java.ee.common.serialization;

import lombok.Builder;

public class ObjectSerializer<T> {
    private final BaseSerializer baseSerializer;
    private final Class<T> objectType;

    @Builder
    public ObjectSerializer(Class<T> objectType, BaseSerializer baseSerializer) {
        this.objectType = objectType;
        this.baseSerializer = baseSerializer;
    }

    public byte[] toByteArr(T object) {
        return baseSerializer.toByteArr(object);
    }

    public T fromByteArr(byte[] byteArr) {
        return baseSerializer.fromByteArr(byteArr, objectType);
    }

    public String toString(T object) {
        return baseSerializer.toString(object);
    }

    public T fromString(String string) {
        return baseSerializer.fromString(string, objectType);
    }
}
