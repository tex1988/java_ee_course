package com.gitlab.hillel.dnepr.java.ee.oleksii.zinkevych.utils;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class CsvUtils<T> {
    private final List<Field> fields;
    private final ReflectionUtils<T> reflectionUtils;

    public CsvUtils(Class<T> clazz) {
        this.fields = ReflectionUtils.addClassFields(clazz);
        this.reflectionUtils = new ReflectionUtils<>(clazz);
    }

    public T getEntityFromRecords(List<String[]> recordsList) {
        T result = reflectionUtils.createEntity();
        for (String[] record : recordsList) {
            for (Field field : this.fields) {
                if (record[0].equals(field.getName())) {
                    reflectionUtils.setFieldValue(field, result, record[1]);
                }
            }
        }
        return result;
    }

    public List<String[]> getCsvRecords(T entity) {
        List<String[]> result = new ArrayList<>();
        this.fields.forEach(field -> {
            String[] record = {field.getName(), reflectionUtils.getFieldValue(field, entity)};
            result.add(record);
        });
        return result;
    }
}
