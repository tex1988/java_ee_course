package com.gitlab.hillel.dnepr.java.ee.common.serialization;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.Objects;

public class JacksonSerializer implements BaseSerializer {
    private final ObjectMapper objectMapper;

    /**
     * Creates default JSON serializer with disabled "DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES"
     */
    public JacksonSerializer() {
        this(createDefaultObjectMapper());
    }

    public JacksonSerializer(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    private static ObjectMapper createDefaultObjectMapper() {
        final ObjectMapper result = new ObjectMapper();
        result.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        return result;
    }

    @Override
    public byte[] toByteArr(Object object) {
        try {
            if (Objects.isNull(object)) {
                throw new IllegalArgumentException("Object is undefined");
            }
            return objectMapper.writeValueAsBytes(object);
        } catch (JsonProcessingException | IllegalArgumentException e) {
            throw new SerializationException("Failed to serialize object", e);
        }
    }

    @Override
    public <T> T fromByteArr(byte[] byteArr, Class<T> objectType) {
        try {
            if (byteArr == null || byteArr.length == 0) {
                throw new IllegalArgumentException("Object byte arr is undefined");
            }
            if (Objects.isNull(objectType)) {
                throw new IllegalArgumentException("Object type is undefined");
            }
            return objectMapper.readValue(byteArr, objectType);
        } catch (IOException | IllegalArgumentException e) {
            throw new SerializationException("Failed to deserialize object", e);
        }
    }
}
