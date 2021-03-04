package com.gitlab.hillel.dnepr.java.ee.oleksii.zinkevych.serializer;

import com.gitlab.hillel.dnepr.java.ee.oleksii.zinkevych.serializer.common.Serializer;
import com.gitlab.hillel.dnepr.java.ee.oleksii.zinkevych.serializer.exceptions.RepositorySerializerException;
import lombok.extern.slf4j.Slf4j;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

@Slf4j
public class JavaObjectSerializer<T, ID> implements Serializer<T, ID> {
    @Override
    public byte[] serialize(T entity) {
        byte[] result;
        try (ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
             ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteArrayOutputStream)) {
            objectOutputStream.writeObject(entity);
            result = byteArrayOutputStream.toByteArray();
        } catch (IOException e) {
            LOGGER.error("Exception: ", e);
            throw new RepositorySerializerException(e);
        }
        return result;
    }

    @SuppressWarnings("unchecked")
    @Override
    public T deserialize(byte[] bytes) {
        T result;
        try (ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(bytes);
             ObjectInputStream objectInputStream = new ObjectInputStream(byteArrayInputStream)) {
            result = (T) objectInputStream.readObject();
        } catch (IOException | ClassNotFoundException e) {
            LOGGER.error("Exception: ", e);
            throw new RepositorySerializerException(e);
        }
        return result;
    }
}