package com.gitlab.hillel.dnepr.java.ee.common.serialization;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.Optional;

public interface BaseSerializer extends ByteArrSerializer, StringSerializer {
    default <T> byte[] toByteArr(T object) {
        final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        try (final ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteArrayOutputStream)) {
            Objects.requireNonNull(object, "Object is undefined");
            objectOutputStream.writeObject(object);
            return byteArrayOutputStream.toByteArray();
        } catch (IOException | NullPointerException | IllegalArgumentException e) {
            throw new SerializationException("Can't serialize object", e);
        }
    }

    default <T> T fromByteArr(byte[] byteArr, Class<T> objectType) {
        if (byteArr == null || byteArr.length == 0) {
            throw new SerializationException(new IllegalArgumentException("Byte array is undefined"));
        }
        final ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(byteArr);
        try (final ObjectInputStream objectInputStream = new ObjectInputStream(byteArrayInputStream)) {
            Objects.requireNonNull(objectType, "Object type is undefined");
            return Optional.ofNullable(objectInputStream.readObject())
                    .map(objectType::cast)
                    .orElseThrow(() -> new IllegalStateException("Can't cast object"));
        } catch (IOException | NullPointerException | ClassNotFoundException e) {
            throw new SerializationException("Can't deserialize object", e);
        }
    }

    default <T> String toString(T object) {
        return new String(toByteArr(object), StandardCharsets.UTF_8);
    }

    default <T> T fromString(String string, Class<T> objectType) {
        return fromByteArr(string.getBytes(StandardCharsets.UTF_8), objectType);
    }
}

