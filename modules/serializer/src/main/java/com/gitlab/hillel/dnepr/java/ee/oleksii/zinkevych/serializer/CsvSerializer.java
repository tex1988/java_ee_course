package com.gitlab.hillel.dnepr.java.ee.oleksii.zinkevych.serializer;

import com.gitlab.hillel.dnepr.java.ee.oleksii.zinkevych.serializer.common.Serializer;
import com.gitlab.hillel.dnepr.java.ee.oleksii.zinkevych.serializer.exceptions.RepositorySerializerException;
import com.gitlab.hillel.dnepr.java.ee.oleksii.zinkevych.utils.CsvUtils;
import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;
import com.opencsv.exceptions.CsvException;
import lombok.extern.slf4j.Slf4j;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.List;

@Slf4j
public class CsvSerializer<T, ID> implements Serializer<T, ID> {
    private final CsvUtils<T> csvUtils;

    public CsvSerializer(Class<T> clazz) {
        this.csvUtils = new CsvUtils<>(clazz);
    }

    @Override
    public byte[] serialize(T entity) {
        byte[] result;
        List<String[]> recordsList = csvUtils.getCsvRecords(entity);
        try (ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
             OutputStreamWriter outputStreamWriter = new OutputStreamWriter(byteArrayOutputStream);
             CSVWriter writer = new CSVWriter(outputStreamWriter)) {
            writer.writeAll(recordsList);
            outputStreamWriter.flush();
            result = byteArrayOutputStream.toByteArray();
        } catch (IOException e) {
            LOGGER.error("Exception: ", e);
            throw new RepositorySerializerException(e);
        }
        return result;
    }

    @Override
    public T deserialize(byte[] bytes) {
        List<String[]> recordsList;
        try (ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(bytes);
             InputStreamReader inputStreamReader = new InputStreamReader(byteArrayInputStream);
             CSVReader csvReader = new CSVReader(inputStreamReader)) {
            recordsList = csvReader.readAll();
        } catch (IOException | CsvException e) {
            LOGGER.error("Exception: ", e);
            throw new RepositorySerializerException(e);
        }
        return csvUtils.getEntityFromRecords(recordsList);
    }
}
