package com.gitlab.hillel.dnepr.java.ee.oleksii.zinkevych.data_converter.converter;

import com.gitlab.hillel.dnepr.java.ee.oleksii.zinkevych.serializer.common.Serializer;
import com.gitlab.hillel.dnepr.java.ee.oleksii.zinkevych.utils.exceptions.RepositoryUtilsException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;

@Slf4j
@Component
public class ConverterUtils<T, ID> {
    T deserialize(String path, Serializer<T, ID> serializer) {
        T result;
        File entityFile = new File(path);
        if (entityFile.exists()) {
            byte[] entityFileBytes;
            try {
                entityFileBytes = FileUtils.readFileToByteArray(entityFile);
            } catch (IOException e) {
                LOGGER.error("Exception: ", e);
                throw new RepositoryUtilsException(e);
            }
            result = serializer.deserialize(entityFileBytes);
        } else {
            RepositoryUtilsException e = new RepositoryUtilsException();
            LOGGER.error("Exception: ", e);
            throw e;
        }
        return result;
    }

    void serialize(T entity, String path, Serializer<T, ID> serializer) {
        File entityFile = new File(path);
        byte[] entityFileBytes = serializer.serialize(entity);
        try {
            FileUtils.writeByteArrayToFile(entityFile, entityFileBytes);
        } catch (IOException e) {
            LOGGER.error("Exception: ", e);
            throw new RepositoryUtilsException(e);
        }
    }
}