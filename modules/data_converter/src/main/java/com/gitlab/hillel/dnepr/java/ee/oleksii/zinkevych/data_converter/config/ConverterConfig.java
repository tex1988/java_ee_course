package com.gitlab.hillel.dnepr.java.ee.oleksii.zinkevych.data_converter.config;


import com.gitlab.hillel.dnepr.java.ee.oleksii.zinkevych.data_converter.converter.CsvBinConverter;
import com.gitlab.hillel.dnepr.java.ee.oleksii.zinkevych.data_converter.entity.User;
import com.gitlab.hillel.dnepr.java.ee.oleksii.zinkevych.serializer.CsvSerializer;
import com.gitlab.hillel.dnepr.java.ee.oleksii.zinkevych.serializer.JavaObjectSerializer;
import com.gitlab.hillel.dnepr.java.ee.oleksii.zinkevych.serializer.common.Serializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ConverterConfig {

    @Bean(name = "csvSerializer")
    public Serializer<User, String> csvSerializer() {
        return new CsvSerializer<>(User.class);
    }

    @Bean(name = "javaObjectSerializer")
    public Serializer<User, String> javaObjectSerializer() {
        return new JavaObjectSerializer<>();
    }

    @Bean(name = "csvBinConverter")
    public CsvBinConverter<User, String> csvBinConverter() {
        return new CsvBinConverter<>(csvSerializer(), javaObjectSerializer());
    }
}