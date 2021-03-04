package com.gitlab.hillel.dnepr.java.ee.oleksii.zinkevych.data_converter.converter;

import com.gitlab.hillel.dnepr.java.ee.oleksii.zinkevych.serializer.common.Serializer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component
public class CsvBinConverter<T, ID> {
    private final ConverterUtils<T, ID> utils;
    private final Serializer<T, ID> csvSerializer;
    private final Serializer<T, ID> javaObjectSerializer;

    @Autowired
    public CsvBinConverter(@Qualifier("csvSerializer")
                     Serializer<T, ID> csvSerializer,
                           @Qualifier("javaObjectSerializer")
                     Serializer<T, ID> javaObjectSerializer) {
        this.utils = new ConverterUtils<>();
        this.csvSerializer = csvSerializer;
        this.javaObjectSerializer = javaObjectSerializer;
    }

    public void convertBinToCsv(String sourcePath, String resultPath) {
        T entity = utils.deserialize(sourcePath, javaObjectSerializer);
        utils.serialize(entity, resultPath, csvSerializer);
    }

    public void convertCsvToBin(String sourcePath, String resultPath) {
        T entity = utils.deserialize(sourcePath, csvSerializer);
        utils.serialize(entity, resultPath, javaObjectSerializer);
    }
}
